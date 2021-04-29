package at.timeguess.backend.ui.controllers;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Timer;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import at.timeguess.backend.model.Cube;
import at.timeguess.backend.ui.websockets.WebSocketManager;
import at.timeguess.backend.utils.CDIAutowired;
import at.timeguess.backend.utils.CDIContextRelated;

/**
 *  Controller for displaying {@link Cube} entities.
 *
 */
@Controller
@CDIContextRelated
@Scope("application")
public class CountDownController {

	private int min;
	private int sec;
	
	private Timer timer;
	int delay = 1000; //milliseconds
	
    @CDIAutowired
	private WebSocketManager webSocketManager;
	
	public void startCountDown(int min, int sec) {
		setMin(min);
		setSec(sec);
		
		ActionListener task = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				count();
			}
		};
		
		this.timer = new Timer(delay, task);
		this.timer.start();
	}
	
	public void startCountDown() {		
		ActionListener task = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				count();
			}
		};
		
		this.timer = new Timer(delay, task);
		this.timer.start();
	}
	
	public void endCountDown() {
		this.timer.stop();
	}
	
	public String getCountDown() {
		return getMin() + " : " + getSec();
	}
	
	public void resetCountDown(){
		setMin(0);
		setSec(0);
		webSocketManager.getCountDownChannel().send("countDownUpdate");	
	}

	public void count() {
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
		webSocketManager.getCountDownChannel().send("countDownUpdate");		
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


