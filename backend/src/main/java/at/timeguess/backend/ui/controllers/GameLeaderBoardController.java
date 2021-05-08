package at.timeguess.backend.ui.controllers;

import at.timeguess.backend.model.Game;
import at.timeguess.backend.model.Round;
import at.timeguess.backend.model.Team;
import at.timeguess.backend.model.Validation;
import at.timeguess.backend.services.GameLogicService;
import at.timeguess.backend.services.RoundService;
import at.timeguess.backend.ui.beans.SessionInfoBean;

import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

/**
 * This controller holds the game-content of the logged-in user
 *
 * This class is part of the skeleton project provided for students of the
 * courses "Software Architecture" and "Software Engineering" offered by the
 * University of Innsbruck.
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
    
    private List<Team> teamsInGame;
    
    private Game currentGame;
    
    
    
    @PostConstruct
    private void setup() {
    	this.currentGame = this.webSocketGameController.getCurrentGameForUser(this.sessionInfoBean.getCurrentUser());
    	this.teamsInGame = List.copyOf(currentGame.getTeams());
    }

	public List<Team> getTeamsInGame() {
		return teamsInGame;
	}

	public void setTeamsInGame(List<Team> teamsInGame) {
		this.teamsInGame = teamsInGame;
	}
	
    public Integer calculatePointsOfTeam(Team team) {
    	return roundService.getPointsOfTeamInGame(currentGame, team);
    }

	public Game getCurrentGame() {
		return currentGame;
	}

	public void setCurrentGame(Game currentGame) {
		this.currentGame = currentGame;
	}
    
    public Team computeWinningTeam() {
    	return gameLogic.getTeamWithMostPoints(currentGame);
    }
}
