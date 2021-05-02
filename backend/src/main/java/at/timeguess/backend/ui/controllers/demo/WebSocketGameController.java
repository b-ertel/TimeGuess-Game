package at.timeguess.backend.ui.controllers.demo;

import at.timeguess.backend.events.ConfiguredFacetsEvent;
import at.timeguess.backend.events.ConfiguredFacetsEventListener;
import at.timeguess.backend.model.Cube;
import at.timeguess.backend.model.CubeFace;
import at.timeguess.backend.model.Game;
import at.timeguess.backend.model.Round;
import at.timeguess.backend.model.Team;
import at.timeguess.backend.model.Term;
import at.timeguess.backend.model.User;
import at.timeguess.backend.repositories.TopicRepository;
import at.timeguess.backend.repositories.UserRepository;
import at.timeguess.backend.services.CubeService;
import at.timeguess.backend.services.GameLogicService;
import at.timeguess.backend.services.GameService;
import at.timeguess.backend.services.TermService;
import at.timeguess.backend.ui.beans.NewGameBean;
import at.timeguess.backend.ui.beans.SessionInfoBean;
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
    private SessionInfoBean sessionInfoBean;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TermService termService;
    @Autowired
    private ChatManagerController chatController;
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
    private CubeFace cubeFace;
    private Round controllRound;

    private Map<Game, Team> teams = new HashMap<>();

    private List<User> guessingUsers = new LinkedList<>();
    private List<User> controllingUsers = new LinkedList<>();

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
 		Cube cube = cubeService.getByMacAddress("56:23:89:34:56");
 		
 		listOfGames.put(cube, testgame);
        this.controllRound = new Round();

        testgame.getActualTeams().stream().forEach(team -> teams.put(testgame, team));
        
    }
    
    @PreDestroy
    public void destroy() {
        configuredfacetsEventListener.unsubscribe(this);
    }


    /**
     * When a user logs out, there shouldn't be the possibility to send him terms
     * anymore and hence it should be removed from any undelivered
     * term-recipient-list
     */
    public void synchronizeRecipients() {
            this.guessingUsers.removeIf(user -> !this.chatController.getPossibleRecipients().contains(user));
            this.controllingUsers.removeIf(user -> !this.chatController.getPossibleRecipients().contains(user));
    }
    
    /**
     * A method for processing a {@link ConfiguredFacetsEvent}.
     */
    @Override
    public synchronized void accept(ConfiguredFacetsEvent configuredFacetsEvent) {
        if (listOfGames.keySet().contains(configuredFacetsEvent.getCube())) {
        	startNewRound(listOfGames.get(configuredFacetsEvent.getCube()), configuredFacetsEvent.getCubeFace()); 	
        	this.websocketManager.getNewRoundChannel().send("newRound");
        }
    }
    
    public void startNewRound(Game game, CubeFace cubeFace) {
    	currentRound = gameLogic.startNewRound(game, cubeFace);
    }

    public List<User> getGuessingUsers() {
        return guessingUsers;
    }

    public List<User> getControllingUsers() {
        return controllingUsers;
    }

    public Round getControllRound() {
        return controllRound;
    }
    
    public Round getCurrentRound() {
        return this.currentRound;
    }

	public CubeFace getCubeFace() {
		return cubeFace;
	}
}
