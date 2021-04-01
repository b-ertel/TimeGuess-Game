package at.timeguess.backend.model;

/**
 * this class represents a face of the timeflip, which consists of an activity, the number of points to get if the round was successful
 * and the time to guess the given term
 *
 */
public class CubeFace {
	
	private Integer time;
	
	private Integer points;
	
	private Activity activity;
		
	public Integer getTime() {
		return time;
	}
	
	public void setTime(Integer time) {
		this.time = time;
	}
	
	public Integer getPoints() {
		return points;
	}
	
	public void setPoints(Integer points) {
		this.points = points;
	}
	
	public Activity getActivity() {
		return activity;
	}
	
	public void setActivity(Activity activity) {
		this.activity = activity;
	}
	
}
