package at.timeguess.backend.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;

/**
 * this class represents a face of the timeflip, which consists of an activity, the number of points to get if the round was successful
 * and the time to guess the given term
 *
 */
@Entity
public class CubeFace {
	
	@Id
	private Long id;
	
	private Integer time;
	
	private Integer points;
	
	private Activity activity;
	
	@OneToMany(mappedBy="cubeFaces")
	private Cube cube;
		
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
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Cube getCube() {
		return cube;
	}

	public void setCube(Cube cube) {
		this.cube = cube;
	}
	
}
