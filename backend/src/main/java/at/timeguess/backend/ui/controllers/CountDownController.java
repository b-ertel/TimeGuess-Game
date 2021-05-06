package at.timeguess.backend.ui.controllers;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Timer;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import at.timeguess.backend.model.User;
import at.timeguess.backend.ui.websockets.WebSocketManager;
import at.timeguess.backend.utils.CDIAutowired;
import at.timeguess.backend.utils.CDIContextRelated;

/**
 *  Controller for displaying the remaining time to guess for each round
 *
 */
@Controller
@Scope("session")
@CDIContextRelated
public class CountDownController {
	
	private int min = 0;
	private int sec = 0;
	
	private Timer timer;
	int delay = 1000; //milliseconds
	
    @CDIAutowired
	private WebSocketManager webSocketManager;
	
	/**
	 * starts countDown with given time and for given user
	 * 
	 * @param time to guess
	 * @param user for which the countdown starts
	 */
	public void startCountDown(int time, User user) {
		setMin(time);
		setSec(0);

		ActionListener task = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				count(user);
			}
		};
		this.timer = new Timer(delay, task);
		this.timer.start();
	}
	
	/**
	 * stops countDown
	 */
	public void endCountDown() {
		this.timer.stop();
	}
	
	/**
	 * @return countDown as string to display
	 */
	public String getCountDown() {
		return getMin() + " : " + getSec();
	}

	/**
	 * counting step - informs websocket listener after each update
	 */
	public void count(User user) {
		if (min > 0) {
			if(sec > 0) {
				sec--;
			}
			else {
				min--;
				sec = 59;
			}
		}
		else if (min==0 && sec>0){
			sec--;
		}
		else {
			endCountDown();
		}
		webSocketManager.getCountDownChannel().send("countDownUpdate", user.getId());  
	}
		
	public int getMin() {
		return min;
	}

	public void setMin(int min) {
		this.min = min;
	}

	public int getSec() {
		return sec;
	}

	public void setSec(int sec) {
		this.sec = sec;
	}
}


