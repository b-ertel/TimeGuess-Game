package at.timeguess.backend.ui.controllers;

import at.timeguess.backend.model.Game;
import at.timeguess.backend.model.Round;
import at.timeguess.backend.model.Team;
import at.timeguess.backend.model.Validation;
import at.timeguess.backend.services.GameLogicService;
import at.timeguess.backend.services.RoundService;
import at.timeguess.backend.ui.beans.SessionInfoBean;

import java.util.List;

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
public class GameRoundController {
	
    @Autowired
    private SessionInfoBean sessionInfoBean;
    @Autowired
    private GameManagerController webSocketGameController;
    @Autowired
    private CountDownController countDownController;
    @Autowired
    private RoundService roundService;
    @Autowired
    private GameLogicService gameLogic;
    
    private Round currentRound;
    
    private Round nextRound;
    
    private Game currentGame = null;
    
    private boolean inRound = false;
    
    private boolean inGuessingTeam = false;
    
    /**
     * Method that sets the attributes of class depending on which user is logged in
     */
    public void setup() {
    	this.nextRound = webSocketGameController.getCurrentRoundForUser(sessionInfoBean.getCurrentUser());
    	this.inGuessingTeam = nextRound.getGuessingTeam().getTeamMembers().contains(sessionInfoBean.getCurrentUser());
    	this.currentGame = this.webSocketGameController.getCurrentGameForUser(this.sessionInfoBean.getCurrentUser());
    }
    
    /**
     * Method that sets all important attributes to null
     */
    public void destroy() {
    	this.currentGame = null;
    	this.currentRound = null;
    	this.nextRound = null;
    }

    /**
     * Method to get round that is to be played
     */
    public void getNextRoundInfos() {
    	this.nextRound = webSocketGameController.getCurrentRoundForUser(sessionInfoBean.getCurrentUser());
    	this.inGuessingTeam = nextRound.getGuessingTeam().getTeamMembers().contains(sessionInfoBean.getCurrentUser());
    }
    
    /**
     * Method to start a new round, including count-down
     */
    public void startRound() {
    	this.inRound = true;
    	this.currentRound = webSocketGameController.getCurrentRoundForUser(sessionInfoBean.getCurrentUser());
    	this.countDownController.startCountDown(currentRound.getTime(), sessionInfoBean.getCurrentUser());
    	this.inGuessingTeam = currentRound.getGuessingTeam().getTeamMembers().contains(sessionInfoBean.getCurrentUser());
    }
    
    /**
     * Method to end a round and the count-down
     */
    public void endRound() {
    	this.countDownController.endCountDown();
    	setInRound(false);
    }
    
    /**
     * Method to end a round through count-down
     */
    public void endRoundThroughCountDown() {
    	incorrectRound();
    	setInRound(false);
    }
    
    public Round getCurrentRound() {
    	return this.currentRound;
    }
    
    public Round getNextRound() {
    	return this.nextRound;
    }
    
    public void setInRound(boolean inRound) {
		this.inRound = inRound;
	}

	public boolean getInRound() {
    	return this.inRound;
    }
    
    public void setInGuessingTeam(boolean bool) {
    	this.inGuessingTeam=bool;
    }

	public boolean isInGuessingTeam() {
		return inGuessingTeam;
	}
    
	/**
	 * Method to validate a round as correct. Gamemanagercontroller saves round correct.
	 */
    public void correctRound() {
    	Game game = webSocketGameController.getCurrentGameForUser(sessionInfoBean.getCurrentUser());
    	webSocketGameController.validateRoundOfGame(game, Validation.CORRECT);
    }
    
    /**
     * Method to validate a round as incorrect. Gamemanagercontroller saves round incorrect.
     */
    public void incorrectRound() {
    	Game game = webSocketGameController.getCurrentGameForUser(sessionInfoBean.getCurrentUser());
    	webSocketGameController.validateRoundOfGame(game, Validation.INCORRECT);
    }
    
    /**
     * Method to validate a round as cheated. Gamemanagercontroller saves round cheated.
     */
    public void cheatedRound() {
    	Game game = webSocketGameController.getCurrentGameForUser(sessionInfoBean.getCurrentUser());
    	webSocketGameController.validateRoundOfGame(game, Validation.CHEATED);    
    }
      
    public Game getCurrentGame() {
    	return this.currentGame;
    }
    
    /**
     * Method to get a list of all teams in the current game
     * @return list of teams
     */
	public List<Team> getTeamsInGame() {
		return List.copyOf(currentGame.getTeams());
	}

	/**
	 * Method to calculate the points a certain team has reached in the current game
	 * @param team of which points are to be estimated
	 * @return points
	 */
    public Integer calculatePointsOfTeam(Team team) {
    	return roundService.getPointsOfTeamInGame(currentGame, team);
    }
    
    /**
     * Method to check which team has won the current game
     * @return winning team
     */
    public Team computeWinningTeam() {
    	Team winningTeam = gameLogic.getTeamWithMostPoints(currentGame);
    	this.currentGame = null;
    	this.currentRound = null;
    	this.nextRound = null;
        return winningTeam;
    	
    }
 

}
