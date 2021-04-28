package at.timeguess.backend.ui.controllers;

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
	private boolean count;
	
    @CDIAutowired
	private WebSocketManager webSocketManager;
	
	public void startCountdown(int min, int sec) {
		this.min = min;
		this.sec= sec;
		run();
		System.out.println(getCountDown());
		count();
	}
	
	public void count() {
		
			while(min >= 0) {
				while(sec > 0) {
					if(count == false) {
						break;
					}
					sec--;
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					webSocketManager.getCountDownChannel().send("countDownUpdate");
				
					if(min==0) {
						break;
					}
				}
				min--;
				sec = 60;
			}

	}
	
	
	public void setCountDown(int min, int sec){
		setMin(min);
		setSec(sec);
	}
	
	
	public void run() {
		this.count=true;
	}
	
	public void stop() {
		this.count=false;
	}
	
	public String getCountDown() {
		return getMin() + " : " + getSec();
	}

	/*
	public static void main(String args[]) {
		System.out.println("Does this work?");
		CountDownController controller = new CountDownController();
		controller.startCountdown(1, 0);
	}
*/

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


