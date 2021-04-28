package at.timeguess.backend.ui.controllers;

import java.util.Set;
import java.util.function.Consumer;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import at.timeguess.backend.events.ConfiguredFacetsEvent;
import at.timeguess.backend.events.ConfiguredFacetsEventListener;
import at.timeguess.backend.model.Cube;
import at.timeguess.backend.model.CubeFace;
import at.timeguess.backend.model.Game;
import at.timeguess.backend.model.Round;
import at.timeguess.backend.model.exceptions.AllTermsUsedInGameException;
import at.timeguess.backend.repositories.TopicRepository;
import at.timeguess.backend.services.CubeService;
import at.timeguess.backend.services.GameLogicService;
import at.timeguess.backend.services.GameService;
import at.timeguess.backend.ui.beans.NewGameBean;
import at.timeguess.backend.ui.websockets.WebSocketManager;
import at.timeguess.backend.utils.CDIAutowired;
import at.timeguess.backend.utils.CDIContextRelated;

@Component
@Scope("session")
@CDIContextRelated
public class PlayRoundController implements Consumer<ConfiguredFacetsEvent> {

	@Autowired
	NewGameBean newGameBean;
	@Autowired
	TopicRepository topicRepo;
	@Autowired
	GameService gameService;
	@Autowired
	CubeService cubeService;
	
	@Autowired
	GameLogicService gameLogic;
	
	@Autowired
    private ConfiguredFacetsEventListener configuredfacetsEventListener;
    @CDIAutowired
    private WebSocketManager websocketManager;

	private Game game;
	private Cube cube;
	private CubeFace cubeFace;
	private Round currentRound;
	
	
	
	
	@PostConstruct
    public void init() {
        configuredfacetsEventListener.subscribe(this);
        newGameBean.setGameName("TestGame");
		newGameBean.setMaxPoints(10);
		newGameBean.setTopic(topicRepo.findById((long) 1).get());
		newGameBean.createGame();
		this.game = gameService.loadGame((long) 8);
		game.setTeams(gameService.loadGame((long) 1).getTeams());
		game.getTeams().addAll(gameService.loadGame((long) 2).getTeams());
		game.setRoundNr(0);
		cube = cubeService.getByMacAddress("56:23:89:34:56");
	}
	
	@PreDestroy
    public void destroy() {
        configuredfacetsEventListener.unsubscribe(this);
    }
	
	/**
     * A method for processing a {@link ConfiguredFacetsEvent}.
     */
    @Override
    public synchronized void accept(ConfiguredFacetsEvent configuredFacetsEvent) {
        if (cube.equals(configuredFacetsEvent.getCube())) {
            cubeFace = configuredFacetsEvent.getCubeFace();
            websocketManager.getNewRoundChannel().send("startRound");
        }
    }
	
    
    public void startNewRound() {
    	currentRound = gameLogic.startNewRound(game, cubeFace);
    }
	
	public void setGame(Game game) {
		this.game = game;
	}
	
	public Game getGame() {
		return this.game;
	}

	public Cube getCube() {
		return cube;
	}

	public void setCube(Cube cube) {
		this.cube = cube;
	}	
	
	public CubeFace getCubeFace() {
        return cubeFace;
    }

    public void setCubeFace(CubeFace cubeFace) {
        this.cubeFace = cubeFace;
    }

	public Round getCurrentRound() {
		return currentRound;
	}

	public void setCurrentRound(Round currentRound) {
		this.currentRound = currentRound;
	}
    
    
	
	
}
