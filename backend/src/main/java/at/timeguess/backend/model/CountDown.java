package at.timeguess.backend.model;

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
	
	
}
