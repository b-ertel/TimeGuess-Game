package at.timeguess.backend.ui.controllers.demo;

import at.timeguess.backend.events.ConfiguredFacetsEvent;
import at.timeguess.backend.events.ConfiguredFacetsEventListener;
import at.timeguess.backend.model.Cube;
import at.timeguess.backend.model.CubeFace;
import at.timeguess.backend.model.Game;
import at.timeguess.backend.model.Round;
import at.timeguess.backend.model.Team;
import at.timeguess.backend.model.User;
import at.timeguess.backend.repositories.TopicRepository;
import at.timeguess.backend.repositories.UserRepository;
import at.timeguess.backend.services.CubeService;
import at.timeguess.backend.services.GameLogicService;
import at.timeguess.backend.services.GameService;
import at.timeguess.backend.services.TermService;
import at.timeguess.backend.ui.beans.NewGameBean;
import at.timeguess.backend.ui.beans.SessionInfoBean;
import at.timeguess.backend.ui.controllers.CountDownController;
import at.timeguess.backend.ui.websockets.WebSocketManager;
import at.timeguess.backend.utils.CDIAutowired;
import at.timeguess.backend.utils.CDIContextRelated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import java.util.*;
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
public class WebSocketGameController implements Consumer<ConfiguredFacetsEvent> {
	@Autowired
	private NewGameBean newGameBean;
	@Autowired
	private TopicRepository topicRepo;
    @Autowired
    private ChatManagerController chatController;
    @Autowired
    private CountDownController countDownController;
    @Autowired
    private GameService gameService;
    @Autowired
	CubeService cubeService;
    @CDIAutowired
    private WebSocketManager websocketManager;
    @Autowired
    private ConfiguredFacetsEventListener configuredfacetsEventListener;
    @Autowired
	GameLogicService gameLogic;

    private Round currentRound;
    private Round controllRound;
    private Game currentGame;

    private Map<Cube, Game> listOfGames = new HashMap<>();

    @PostConstruct
    public void setup() {
    	configuredfacetsEventListener.subscribe(this);
    	newGameBean.setGameName("TestGame");
 		newGameBean.setMaxPoints(10);
 		newGameBean.setTopic(topicRepo.findById((long) 1).get());
 		newGameBean.createGame();
 		Game testgame = gameService.loadGame((long) 8);
 		testgame.setTeams(gameService.loadGame((long) 1).getTeams());
 		testgame.getTeams().addAll(gameService.loadGame((long) 2).getTeams());
 		testgame.setRoundNr(0);

 		
 		List<User> users = new ArrayList<>();
 		List<String> usernames = new ArrayList<>();
 		testgame.getActualTeams().stream().forEach(team -> users.addAll(team.getTeamMembers()));
 		users.stream().forEach(user -> usernames.add(user.getUsername()));
 		Cube cube = cubeService.getByMacAddress("56:23:89:34:56");
 		
 		listOfGames.put(cube, testgame);
        this.controllRound = new Round();
        this.currentGame = testgame;        
    }
    
    @PreDestroy
    public void destroy() {
        configuredfacetsEventListener.unsubscribe(this);
    }

   
    /**
     * A method for processing a {@link ConfiguredFacetsEvent}.
     * Method is called on facet-event, checks to which game it corresponds and calls startNewMethod() to initialize new round.
     */
    @Override
    public synchronized void accept(ConfiguredFacetsEvent configuredFacetsEvent) {
        if (listOfGames.keySet().contains(configuredFacetsEvent.getCube())) { 
        	startNewRound(listOfGames.get(configuredFacetsEvent.getCube()), configuredFacetsEvent.getCubeFace()); 
        }
    }
    
    /**
     * Method that starts a new Round for a game. It initializes a new round and also calls a method to start the countdown.
     * @param currentGame, game for which round should be started
     * @param cubeFace, face that sets round parameter
     */
    public void startNewRound(Game currentGame, CubeFace cubeFace) {
    	this.currentRound = gameLogic.startNewRound(currentGame, cubeFace);
    	this.websocketManager.getNewRoundChannel().send("newRound", getAllUsernamesOfGameTeams(currentGame.getActualTeams()));
    	countDownController.startCountDown(cubeFace, currentGame);
    }

    public Round getControllRound() {
        return controllRound;
    }
    
    public Round getCurrentRound() {
        return this.currentRound;
    }

	public Game getCurrentGame() {
		return currentGame;
	}

	public void setCurrentGame(Game currentGame) {
		this.currentGame = currentGame;
	}
	
	
	/**
	 * A method to put all usernames of players, that are online, into a list
	 * @param listOfTeams
	 * @return list of usernames
	 */
	private List<String> getAllUsernamesOfGameTeams(List<Team> listOfTeams){
		List<String> usernames = new ArrayList<>();
		List<User> users = new ArrayList<>();
		for(Team team : listOfTeams) {
			users.addAll(team.getTeamMembers());
		}
		users.stream().forEach(user -> usernames.add(user.getUsername()));
		return usernames;
	}
	
}
