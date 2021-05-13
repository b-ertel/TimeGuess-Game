package at.timeguess.backend.ui.controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import at.timeguess.backend.events.ChannelPresenceEvent;
import at.timeguess.backend.events.ChannelPresenceEventListener;
import at.timeguess.backend.events.ConfiguredFacetsEvent;
import at.timeguess.backend.events.ConfiguredFacetsEventListener;
import at.timeguess.backend.model.Cube;
import at.timeguess.backend.model.CubeFace;
import at.timeguess.backend.model.Game;
import at.timeguess.backend.model.GameState;
import at.timeguess.backend.model.Round;
import at.timeguess.backend.model.Team;
import at.timeguess.backend.model.User;
import at.timeguess.backend.model.Validation;
import at.timeguess.backend.services.CubeService;
import at.timeguess.backend.services.GameLogicService;
import at.timeguess.backend.services.GameService;
import at.timeguess.backend.services.RoundService;
import at.timeguess.backend.ui.beans.MessageBean;
import at.timeguess.backend.ui.websockets.WebSocketManager;
import at.timeguess.backend.utils.CDIAutowired;
import at.timeguess.backend.utils.CDIContextRelated;

/**
 * This controller is responsible for showing a term in the game window with websockets
 * and managing all currently running games.
 */
@Controller
@Scope("application")
@CDIContextRelated
public class GameManagerController {

    @Autowired
    private GameService gameService;
    @Autowired
    private RoundService roundService;
    @Autowired
    private CubeService cubeService;
    @Autowired
    private GameLogicService gameLogic;
  //  @Autowired
 //   private CubeStatusController cubeStatusController;
    @CDIAutowired
    private WebSocketManager websocketManager;
    @Autowired
    private ConfiguredFacetsEventListener configuredfacetsEventListener;
    @Autowired
    private ChannelPresenceEventListener channelPresenceEventListener;
    @Autowired
    private MessageBean messageBean;

    // cannot implement two different Consumers!
    private Consumer<ConfiguredFacetsEvent> consumerConfiguredFacetsEvent =
            (cfEvent) -> GameManagerController.this.onConfiguredFacetsEvent(cfEvent);
    private Consumer<ChannelPresenceEvent> consumerChannelPresenceEvent =
            (cpEvent) -> GameManagerController.this.onChannelPresenceEvent(cpEvent);

    private Map<Game, Round> currentRound = new ConcurrentHashMap<>();
    private Map<Game, Boolean> midValidation = new ConcurrentHashMap<>();
    private Map<Game, Boolean> activeRound = new ConcurrentHashMap<>(); // indicated if there is played a round currently in the game
    private Map<Cube, Game> listOfGames = new HashMap<>();

    @PostConstruct
    public void setup() {
        configuredfacetsEventListener.subscribe(consumerConfiguredFacetsEvent);
        channelPresenceEventListener.subscribe("newRoundChannel", consumerChannelPresenceEvent);

        this.start2TestGames();
    }

    @PreDestroy
    public void destroy() {
        configuredfacetsEventListener.unsubscribe(consumerConfiguredFacetsEvent);
        channelPresenceEventListener.unsubscribe("newRoundChannel", consumerChannelPresenceEvent);
    }

    /**
     * A method for processing a {@link ConfiguredFacetsEvent}. Method is called on facet-event, checks to which game
     * the event belongs and estimates whether a round should start or end
     */
    public synchronized void onConfiguredFacetsEvent(ConfiguredFacetsEvent configuredFacetsEvent) {
        if (listOfGames.keySet().contains(configuredFacetsEvent.getCube())) {
            Game game = listOfGames.get(configuredFacetsEvent.getCube());
            if(!midValidation.get(game)) {
            	if (!activeRound.get(game)) {
                    activeRound.put(game, true);
                    startRoundByCube(game, currentRound.get(game), configuredFacetsEvent.getCubeFace());
                }
                else {
                    activeRound.put(game, false);
                    midValidation.put(game, true);
                    this.websocketManager.getNewRoundChannel().send("endRoundViaFlip", getAllUserIdsOfGameTeams(game.getTeams()));
                }
            }    
        }
    }

    /**
     * A method for processing a {@link ChannelPresenceEvent}. Checks for each game currently to be played whether
     * enough players are present and sets the games state accordingly. Is called by a channel supervisor when players
     * enter or leave the game room.
     * @apiNote Game should be saved here, but cannot for missing authentication context.
     */
    public synchronized void onChannelPresenceEvent(ChannelPresenceEvent channelPresenceEvent) {
        if (!channelPresenceEvent.getChannel().equals("newRoundChannel")) return;

        final Set<Long> userIds = channelPresenceEvent.getUserIds();
        for (Game game : this.listOfGames.values()) {
            Set<Team> teams = game.getTeams();
            int countTeams = teams.size();
            int counter = 0;
            for (Team team : teams) {
                if (team.getTeamMembers().stream().anyMatch(u -> userIds.contains(u.getId()))) counter++;
            }

            game.setStatus(counter < countTeams
                    ? game.getStatus() == GameState.PLAYED ? GameState.HALTED : GameState.VALID_SETUP
                    : GameState.PLAYED);

            // should, but cannot save game here, because authentication is missing from context (event was externally
            // initialized)
            // TODO: save game on next occasion to keep db up-to-date
            // gameService.saveGame(game);
        }
    }

    /**
     * Method that starts a new Round for a game. It initializes a new round and also calls a method to start the
     * countdown.
     * @param currentGame, game for which round should be started
     * @param cubeFace,    face that sets round parameter
     */
    public void startRoundByCube(Game game, Round round, CubeFace cubeFace) {
        this.currentRound.put(game, gameLogic.getCubeInfosIntoRound(round, cubeFace));
        this.websocketManager.getNewRoundChannel().send("startRound", getAllUserIdsOfGameTeams(game.getTeams()));
    }
    
    public void getNextRoundInfo(Game game) {
    	this.currentRound.put(game, gameLogic.getNextRound(game));
    }

    public Round getCurrentRoundOfGame(Game game) {
        return this.currentRound.get(game);
    }

    /**
     * method to get the current round for a given user, called by {@link UserGameController}
     * @param user to find current round
     * @return current round
     */
    public Round getCurrentRoundForUser(User user) {
        Round round = new Round();
        for (Map.Entry<Game, Round> e : currentRound.entrySet()) {
            for (Team t : e.getKey().getTeams()) {
                if (t.getTeamMembers().contains(user)) {
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
    public List<Long> getAllUserIdsOfGameTeams(Set<Team> listOfTeams) {
        List<Long> userIds = new ArrayList<>();
        List<User> users = new ArrayList<>();
        for (Team team : listOfTeams) {
            users.addAll(team.getTeamMembers());
        }
        users.stream().forEach(user -> userIds.add(user.getId()));
        return userIds;
    }

    /**
     * find the game in which a given user is currently playing, called by{@link CouuntDownController}
     * @param user to find current game
     * @return current game
     */
    public Game getCurrentGameForUser(User user) {
        for (Game g : this.listOfGames.values()) {
            for (Team t : g.getTeams()) {
                if (t.getTeamMembers().contains(user)) {
                    return g;
                }
            }
        }
        return null;
    }

    /**
     * Method to add a newly created game to the saved list of currently available games
     * and send notifications to all participating team members, except the game creator.
     * @param game
     */
    public void addGame(Game game) {
        if (game == null) throw new NullPointerException("startGame was called with null game");

        // change cube status
//        cubeStatusController.setInGame(game.getCube().getMacAddress());

        // change game status
        game.setStatus(GameState.VALID_SETUP);
        game = gameService.saveGame(game);

        if (game != null) {
            // add game to map
            listOfGames.put(game.getCube(), game);
            activeRound.put(game, false);
            midValidation.put(game, false);

            // send invitation to all contained team members
            Set<Long> invited = game.getTeams().stream().flatMap(t -> t.getTeamMembers().stream().map(User::getId))
                    .collect(Collectors.toSet());
            websocketManager.getMessageChannel()
                    .send(Map.of("type", "gameInvitation", "name", game.getName(), "id", game.getId()), invited);
        }
    }

    public void validateRoundOfGame(Game game, Validation v) {
        gameLogic.saveLastRound(game, v);
        this.listOfGames.put(getCubeByGame(game), game);
        midValidation.put(game, false);
        if (gameLogic.teamReachedMaxPoints(game, this.currentRound.get(game).getGuessingTeam())) {
            endGame(game);
            this.websocketManager.getNewRoundChannel().send("gameOver", getAllUserIdsOfGameTeams(game.getTeams()));
        } else { 
        	if (!gameLogic.stillTermsAvailable(game)) {
        		Team winningTeam = gameLogic.getTeamWithMostPoints(game);
                game.setMaxPoints(roundService.getPointsOfTeamInGame(game, winningTeam));
                endGame(game);
                this.websocketManager.getNewRoundChannel().send("termsOver", getAllUserIdsOfGameTeams(game.getTeams()));
        	} else {
            	getNextRoundInfo(this.listOfGames.get(getCubeByGame(game)));
                this.websocketManager.getNewRoundChannel().send("validatedRound", getAllUserIdsOfGameTeams(game.getTeams()));
            }

        }
    }

    private Cube getCubeByGame(Game game) {
        for (Entry<Cube, Game> e : this.listOfGames.entrySet()) {
            if (e.getValue().equals(game)) {
                return e.getKey();
            }
        }
        return null;
    }

    public void setActiveRoundFalse(Game game) {
        this.activeRound.put(game, false);
    }
    
    public void setMidValidationTrue(Game game) {
    	this.midValidation.put(game, true);
    }

    public void endGame(Game game) {
        game.setStatus(GameState.FINISHED);
        gameService.saveGame(game);
        this.listOfGames.remove(getCubeByGame(game));
        this.currentRound.remove(game);
        this.activeRound.remove(game);
    }

    private void start2TestGames() {
        Game testgame1 = gameService.loadGame(8L);
        Cube cube = cubeService.getByMacAddress("56:23:89:34:56");

        System.out.println("players for testgame 1:");
        for (Team t : testgame1.getTeams()) {
            for (User u : t.getTeamMembers()) {
                System.out.println(u.getUsername());
            }
        }

        Game testgame2 = gameService.loadGame(9L);
        Cube cube2 = cubeService.getByMacAddress("22:23:89:90:56");

        System.out.println("players for testgame 2:");
        for (Team t : testgame2.getTeams()) {
            for (User u : t.getTeamMembers()) {
                System.out.println(u.getUsername());
            }
        }
        System.out.println(testgame1.getMaxPoints());
        listOfGames.put(cube, testgame1);
        listOfGames.put(cube2, testgame2);
        activeRound.put(testgame1, false);
        activeRound.put(testgame2, false);
        midValidation.put(testgame1, false);
        midValidation.put(testgame2, false);
        getNextRoundInfo(testgame1);
        getNextRoundInfo(testgame2);
    }

	/**
	 * finds a game with a given cube
	 * 
	 * @param cube to find the game
	 * @return the game
	 */
	public Game getCurrentGameForCube(Cube cube) {
		return this.listOfGames.get(cube);
	}
	
	private String message;
	
	public void healthNotification(String message, Cube cube) {
		messageBean.alertInformation("Health Message", message);
		this.message = message;
		System.out.println(message);
		Game game = getCurrentGameForCube(cube);
		System.out.println(game);
		List<Long> usersToNotify = getAllUserIdsOfGameTeams(game.getTeams());
		System.out.println(usersToNotify);
		
		websocketManager.getNewRoundChannel().send("healthMessage", usersToNotify);
	}
	
	public void displayMessage() {
		messageBean.alertInformation("Health Message", message);
	}
}
