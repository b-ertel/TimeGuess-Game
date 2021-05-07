package at.timeguess.backend.ui.controllers;

import at.timeguess.backend.model.Game;
import at.timeguess.backend.model.Round;
import at.timeguess.backend.model.Validation;
import at.timeguess.backend.ui.beans.SessionInfoBean;

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
public class UserGameController {
	
    @Autowired
    private SessionInfoBean sessionInfoBean;
    @Autowired
    private GameManagerController webSocketGameController;
    @Autowired
    private CountDownController countDownController;
    
    private Round currentRound;
    
    private boolean inRound = false;
    
    private boolean inGuessingTeam = false;

    
    public void startRound() {
    	this.inRound = true;
    	this.currentRound = webSocketGameController.getCurrentRoundForUser(sessionInfoBean.getCurrentUser());
    	this.countDownController.startCountDown(currentRound.getTime(), sessionInfoBean.getCurrentUser());
    	this.inGuessingTeam = currentRound.getGuessingTeam().getTeamMembers().contains(sessionInfoBean.getCurrentUser());
    }
    
    public void endRound() {
    	this.countDownController.endCountDown();
    	setInRound(false);
    }
    
    public void endRoundThroughCountDown() {
    	incorrectRound();
    	setInRound(false);
    }
    
    public Round getCurrentRound() {
    	return this.currentRound;
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
    
    public void correctRound() {
    	Game game = webSocketGameController.getCurrentGameForUser(sessionInfoBean.getCurrentUser());
    	webSocketGameController.validateRoundOfGame(game, Validation.CORRECT);
    }
    
    public void incorrectRound() {
    	Game game = webSocketGameController.getCurrentGameForUser(sessionInfoBean.getCurrentUser());
    	webSocketGameController.validateRoundOfGame(game, Validation.INCORRECT);
    }
    
    public void cheatedRound() {
    	Game game = webSocketGameController.getCurrentGameForUser(sessionInfoBean.getCurrentUser());
    	webSocketGameController.validateRoundOfGame(game, Validation.CHEATED);    
    }

}
