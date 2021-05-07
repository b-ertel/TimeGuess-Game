package at.timeguess.backend.ui.controllers;

import at.timeguess.backend.model.Round;
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

    
    public void startRound() {
    	this.inRound = true;
    	this.currentRound = webSocketGameController.getCurrentRoundForUser(sessionInfoBean.getCurrentUser());
    	this.countDownController.startCountDown(currentRound.getTime(), sessionInfoBean.getCurrentUser());
    	
    }
    
    public void endRound() {
    	this.countDownController.endCountDown();
    	setInRound(false);
    }
    
    public void endRoundThroughCountDown() {
    	setInRound(false);
    }
    
    public Round getCurrentRound() {
    	return this.currentRound;
    }
    
    public void setInRound(boolean inRound) {
    	System.out.println("set false");
		this.inRound = inRound;
	}

	public boolean getInRound() {
    	return this.inRound;
    }
    
    public boolean isGuessingPlayer() {
    	return !webSocketGameController.getCurrentRoundForUser(sessionInfoBean.getCurrentUser()).getGuessingUser().getUsername().equals(sessionInfoBean.getCurrentUserName());
    }

}
