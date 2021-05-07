package at.timeguess.backend.ui.controllers;

import at.timeguess.backend.events.ConfiguredFacetsEvent;
import at.timeguess.backend.events.ConfiguredFacetsEventListener;
import at.timeguess.backend.model.Cube;
import at.timeguess.backend.model.CubeFace;
import at.timeguess.backend.model.Game;
import at.timeguess.backend.model.Round;
import at.timeguess.backend.model.Team;
import at.timeguess.backend.model.User;

import at.timeguess.backend.model.Validation;
import at.timeguess.backend.services.CubeService;
import at.timeguess.backend.services.GameLogicService;
import at.timeguess.backend.services.GameService;
import at.timeguess.backend.ui.websockets.WebSocketManager;
import at.timeguess.backend.utils.CDIAutowired;
import at.timeguess.backend.utils.CDIContextRelated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;


/**
 * This controller is responsible for showing a term in the game window with websockets
 *
 *
 * This class is part of the skeleton project provided for students of the
 * courses "Software Architecture" and "Software Engineering" offered by the
 * University of Innsbruck.
 */

@Controller
@Scope("application")
@CDIContextRelated
public class GameManagerController implements Consumer<ConfiguredFacetsEvent> {

    @Autowired
    private GameService gameService;
    @Autowired
	private CubeService cubeService;
    @CDIAutowired
    private WebSocketManager websocketManager;
    @Autowired
    private ConfiguredFacetsEventListener configuredfacetsEventListener;
    @Autowired
	private GameLogicService gameLogic;

    private Map<Game, Round> currentRound = new ConcurrentHashMap<>();
    private Map<Game, Boolean> activeRound = new ConcurrentHashMap<>();		// indicated if there is played a round currently in the game
    private Map<Cube, Game> listOfGames = new HashMap<>();

    @PostConstruct
    public void setup() {
    	configuredfacetsEventListener.subscribe(this);
 		Game testgame1 = gameService.loadGame(8L);
 		Cube cube = cubeService.getByMacAddress("56:23:89:34:56");
 		
 		System.out.println("players for testgame 1:");
 		for(Team t : testgame1.getTeams()) {
 			for(User u : t.getTeamMembers()) {
 				System.out.println(u.getUsername());
 			}
 		}
 				
 		Game testgame2 = gameService.loadGame(9L);
 		Cube cube2 = cubeService.getByMacAddress("22:23:89:90:56");
 			
 		System.out.println("players for testgame 2:");
 		for(Team t : testgame2.getTeams()) {
 			for(User u : t.getTeamMembers()) {
 				System.out.println(u.getUsername());
 			}
 		}

 		listOfGames.put(cube, testgame1);
 		listOfGames.put(cube2, testgame2); 
        activeRound.put(testgame1, false);
        activeRound.put(testgame2, false);
    }
    
    @PreDestroy
    public void destroy() {
        configuredfacetsEventListener.unsubscribe(this);
    }

    /**
     * A method for processing a {@link ConfiguredFacetsEvent}.
     * Method is called on facet-event, checks to which game the event belongs and estimates whether a round should start or end
     */
    @Override
    public synchronized void accept(ConfiguredFacetsEvent configuredFacetsEvent) {
        if (listOfGames.keySet().contains(configuredFacetsEvent.getCube())) {
        	Game game = listOfGames.get(configuredFacetsEvent.getCube());
        	if(!activeRound.get(game)) {
        		activeRound.put(game, true);
        		startNewRound(game, configuredFacetsEvent.getCubeFace()); 
        	} else {
        		activeRound.put(game, false);
        		this.websocketManager.getNewRoundChannel().send("endRoundViaFlip", getAllUserIdsOfGameTeams(game.getTeams()));
        	}    	
        }
    }
    
    /**
     * Method that starts a new Round for a game. It initializes a new round and also calls a method to start the countdown.
     * @param currentGame, game for which round should be started
     * @param cubeFace, face that sets round parameter
     */
    public void startNewRound(Game currentGame, CubeFace cubeFace) {
    	this.currentRound.put(currentGame, gameLogic.startNewRound(currentGame, cubeFace));
    	this.websocketManager.getNewRoundChannel().send("startRound", getAllUserIdsOfGameTeams(currentGame.getTeams()));
    }
  
    public Round getCurrentRoundOfGame(Game game) {
        return this.currentRound.get(game);
    }
    
    /**
     * method to get the current round for a given user, called by {@link UserGameController}
     * 
     * @param user to find current round
     * @return current round
     */
    public Round getCurrentRoundForUser(User user) {
    	
    	Round round = new Round();
   
    	for(Map.Entry<Game, Round> e : currentRound.entrySet()) {
    		for(Team t : e.getKey().getTeams()) {
    			if(t.getTeamMembers().contains(user)) {
    				round = e.getValue();
    			}
    		}
    	}
    	return round;
    }

	/**
	 * A method to put all usernames of players, that are online, into a list
	 * @param listOfTeams
	 * @return list of usernames
	 */
	private List<Long> getAllUserIdsOfGameTeams(Set<Team> listOfTeams){
		List<Long> userIds = new ArrayList<>();
		List<User> users = new ArrayList<>();
		for(Team team : listOfTeams) {
			users.addAll(team.getTeamMembers());
		}
		users.stream().forEach(user -> userIds.add(user.getId()));
		return userIds;
	}
	
	   
    /**
     * find the game in which a given user is currently playing, called by{@link CouuntDownController}
     * 
     * @param user to find current game
     * @return current game
     */
	public Game getCurrentGameForUser(User user) {
		for(Game g : this.listOfGames.values()) {
			for(Team t : g.getTeams()) {
				if(t.getTeamMembers().contains(user)) {
					return g;
				}
			}
		}
		return null;
	}
	
	public void validateRoundOfGame(Game game, Validation v) {
		gameLogic.saveLastRound(game, v);
		this.listOfGames.put(getCubeByGame(game), game);	
		this.websocketManager.getNewRoundChannel().send("validatedRound", getAllUserIdsOfGameTeams(game.getTeams()));
	}
	
	
	private Cube getCubeByGame(Game game) {
		for(Entry<Cube, Game> e : this.listOfGames.entrySet()) {
			if(e.getValue().equals(game)) {
				return e.getKey();
			}
		}
		return null;
	}
	
	
	public void setActiveRoundFalse(Game game) {
		this.activeRound.put(game,false);
	}

	
}
