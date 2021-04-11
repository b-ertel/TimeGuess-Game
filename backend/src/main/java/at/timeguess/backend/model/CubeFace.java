package at.timeguess.backend.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

/**
 * this class represents a face of the timeflip, which consists of an activity, the number of points to get if the round was successful
 * and the time to guess the given term
 *
 */
@Entity
public class CubeFace {
	
	@Id
	private String id;
	
	private Integer time;
	
	private Integer points;
	
	@Enumerated(EnumType.STRING)
	private Activity activity;
	
	@OneToMany(mappedBy="cubeface")
	private List<Configuration> configs = new ArrayList<>();
		
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
	
	@Enumerated(EnumType.STRING)
	public Activity getActivity() {
		return activity;
	}
	
	public void setActivity(Activity activity) {
		this.activity = activity;
	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	public void addConfiguration(Configuration config) {
		this.configs.add(config);
	}
	
	public List<Configuration> getConfigs() {
		return this.configs;
	}
	
}
