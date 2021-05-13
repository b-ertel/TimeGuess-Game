package at.timeguess.backend.ui.controllers;

import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import at.timeguess.backend.model.Game;
import at.timeguess.backend.model.Team;
import at.timeguess.backend.services.GameLogicService;
import at.timeguess.backend.services.RoundService;
import at.timeguess.backend.ui.beans.SessionInfoBean;

/**
 * This controller holds the game-content of the logged-in user This class is part of the skeleton project provided for
 * students of the courses "Software Architecture" and "Software Engineering" offered by the University of Innsbruck.
 */
@Controller
@Scope("session")
public class GameLeaderBoardController {

    @Autowired
    private SessionInfoBean sessionInfoBean;
    @Autowired
    private GameManagerController webSocketGameController;
    @Autowired
    private RoundService roundService;
    @Autowired
    private GameLogicService gameLogic;
    
    private Game currentGame;
    
    @PostConstruct
    public void setup() {
    	if(this.webSocketGameController.getCurrentGameForUser(this.sessionInfoBean.getCurrentUser())!=null){
    		this.currentGame = this.webSocketGameController.getCurrentGameForUser(this.sessionInfoBean.getCurrentUser());
    	}
    }
    
	public List<Team> getTeamsInGame() {
		return List.copyOf(currentGame.getTeams());
	}

	
    public Integer calculatePointsOfTeam(Team team) {
    	return roundService.getPointsOfTeamInGame(currentGame, team);
    }

	public Game getCurrentGame() {
		return this.currentGame;
	}

    
    public Team computeWinningTeam() {
    	return gameLogic.getTeamWithMostPoints(currentGame);
    }
}
