package at.timeguess.backend.model.utils;

import at.timeguess.backend.model.Team;

public class TeamScore {

	private Team team;
	
	private Integer score;
	
	public TeamScore(Team team, Integer score) {
		this.team = team;
		this.score = score;
	}

	public Team getTeam() {
		return team;
	}

	public void setTeam(Team team) {
		this.team = team;
	}

	public Integer getScore() {
		return score;
	}

	public void setScore(Integer score) {
		this.score = score;
	}
	
	
}
