package at.timeguess.backend.ui.controllers.demo;

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
public class WebSocketUserGameController {
    @Autowired
    private SessionInfoBean sessionInfoBean;
    @Autowired
    private GameMangerController webSocketGameController;
    
    private Round currentRound;

    
    public void startNewRound() {
    	this.currentRound = webSocketGameController.getCurrentRoundForUser(sessionInfoBean.getCurrentUser());
    }
    
    public Round getCurrentRound() {
    	return this.currentRound;
    }
    
    public boolean isGuessingPlayer() {
    	return !webSocketGameController.getCurrentRoundForUser(sessionInfoBean.getCurrentUser()).getGuessingUser().getUsername().equals(sessionInfoBean.getCurrentUserName());
    }

}
