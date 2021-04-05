package at.timeguess.backend.model;

import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

@Entity
public class Game {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column
	private String name;
	
	private int maxPoints;
	
    private GameState status; // more finegrained replacement for status
	
	private int roundNr;
	
	@OneToMany(mappedBy = "game")
    private Set<GameTeam> teams;
	
	@ManyToOne
	private Topic topic; 
	
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

	public GameState getStatus() {
		return status;
	}

	public void setStatus(GameState status) {
		this.status = status;
	}

	public int getRoundNr() {
		return roundNr;
	}

	public void setRoundNr(int roundNr) {
		this.roundNr = roundNr;
	}

	public Set<Team> getTeams() {
		return teams.stream().map(GameTeam::getTeam).collect(Collectors.toSet());
	}

	public void setTeams(Set<Team> teams) {
		this.teams = teams.stream().map(t -> new GameTeam(this, t)).collect(Collectors.toSet());
	}

	public Topic getTopic() {
		return topic;
	}

	public void setTopic(Topic topic) {
		this.topic = topic;
	}
}
