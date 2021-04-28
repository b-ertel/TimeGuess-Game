package at.timeguess.backend.model;

import java.util.Timer;
import java.util.TimerTask;

public class CountDown {

	private int minutes;
	private int seconds;
	
	public CountDown() {
		setSeconds(0);
	}
	

	
	public CountDown(int minutes) {
		setMinutes(minutes);
		setSeconds(0);
	}
	
	public int getMinutes() {
		return minutes;
	}
	
	public void setMinutes(int minutes) {
		this.minutes = minutes;
	}
	
	public int getSeconds() {
		return seconds;
	}
	
	public void setSeconds(int seconds) {
		this.seconds = seconds;
	}
	
	@Override
	public String toString() {
		return getMinutes() + " : " +  getSeconds();
	}
	/*
	private int sec;
	private int min;

	public CountDown(int min, int sec) {
		Timer timer = new Timer();
		this.min = min;
		this.sec = sec;
		TimerTask task = new MyTimerTask();
	
		timer.schedule(task, 0, 1000);

	}
	
	public class MyTimerTask extends TimerTask {
	
		@Override
		public void run() {
			System.out.println(getMin() + " : " + getSec());
			
			if (min >= 0) {
				if(sec > 0) {
					System.out.println(sec);
					setSec(getSec()-1);
				}
			}
			if(sec==0 && min > 0)
				min--;
				System.out.println("ha");
				sec = 60;
			

			if (min == 0 && sec == 0)
				System.exit(0);
		}
			
	}
	
	public int getSec() {
		return sec;
	}

	public void setSec(int sec) {
		this.sec = sec;
	}

	public int getMin() {
		return min;
	}

	public void setMin(int min) {
		this.min = min;
	}

	public static void main(String[] args) {
		new CountDown(1, 0);
	}	*/
}
	
