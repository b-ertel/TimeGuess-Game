package at.timeguess.backend.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Game {
	
	@Id
	private Long id;
	
	@Column
	private String name;
	
	private int maxPoints;
	
	private boolean status;
	
	private int roundNr;

	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getMaxPoints() {
		return maxPoints;
	}

	public void setMaxPoints(int maxPoints) {
		this.maxPoints = maxPoints;
	}

	public boolean isStatus() {
		return status;
	}

	public void setStatus(boolean status) {
		this.status = status;
	}

	public int getRoundNr() {
		return roundNr;
	}

	public void setRoundNr(int roundNr) {
		this.roundNr = roundNr;
	}

}
