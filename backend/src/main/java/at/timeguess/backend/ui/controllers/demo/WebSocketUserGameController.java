package at.timeguess.backend.ui.controllers.demo;

import at.timeguess.backend.model.Term;
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
    private WebSocketGameController webSocketGameController;

    public Term getCurrentTerm() {
        if (this.webSocketGameController.getGuessingUsers().contains(this.sessionInfoBean.getCurrentUser())) {
            return this.webSocketGameController.getCurrentTerm();
        }
        else if (this.webSocketGameController.getControllingUsers().contains(this.sessionInfoBean.getCurrentUser())) {
            return this.webSocketGameController.getControllTerm();
        }
        else {
            Term notInTeamTerm = new Term();
            notInTeamTerm.setName("You are not in a team...");
            return notInTeamTerm;
        }
    }

}
