package at.timeguess.backend.ui.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import at.timeguess.backend.ui.beans.SessionInfoBean;

/**
 *  Controller for displaying the countdown
 *
 */
@Controller
@Scope("session")
public class CountDownDisplayController {
	
	private String countDown = "0 : 0";
	
	@Autowired
	private CountDownController countDownController;
	@Autowired
	private SessionInfoBean sessionBean;
		
	/**
	 * @return countdown if current user is in game   --> to be adjusted for more games
	 */
	public String getCountDown() {
		if(countDownController.getGameMembers().contains(sessionBean.getCurrentUser().getUsername()))
			setCountDown(countDownController.getCountDown());
		
		return this.countDown;
	}
	
	public void setCountDown(String timer) {
		this.countDown = timer;
	}
	
}


