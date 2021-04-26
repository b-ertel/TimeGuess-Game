package at.timeguess.backend.ui.controllers;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import at.timeguess.backend.model.CountDown;
import at.timeguess.backend.model.Cube;
import at.timeguess.backend.ui.websockets.WebSocketManager;
import at.timeguess.backend.utils.CDIAutowired;
import at.timeguess.backend.utils.CDIContextRelated;

/**
 *  Controller for displaying {@link Cube} entities.
 *
 */
@Component
@CDIContextRelated
@Scope("session")
public class CountDownController {

	private CountDown countDown;
	private int minutes;
	
    @CDIAutowired
	private WebSocketManager webSocketManager;
	
	public void startCountdown() {
		this.countDown = new CountDown();
		this.countDown.setMinutes(minutes);
	}
	
	public void count() {
		while(minutes != 0) {
			minutes--;
			this.countDown.setMinutes(minutes);
			webSocketManager.getCubeChannel().send("countDownUpdate");
		}
	}

	
	public static void main(String args[]) {
		System.out.println("Does this work?");
		CountDownController controller = new CountDownController();
		controller.startCountdown();
	}

	public CountDown getCountDown() {
		return countDown;
	}

	public void setCountDown(CountDown countDown) {
		this.countDown = countDown;
	}

	public int getMinutes() {
		return minutes;
	}

	public void setMinutes(int minutes) {
		this.minutes = minutes;
	}

}


