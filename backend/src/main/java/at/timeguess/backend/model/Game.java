package at.timeguess.backend.model;

import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;

import org.springframework.data.domain.Persistable;

import at.timeguess.backend.model.game.GameException;
import at.timeguess.backend.model.game.GameState;

@Entity
public class Game implements Persistable<Long> {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column
	private String name;

	private int maxPoints;

	private boolean status;
	private GameState state;

	

	private int roundNr;

	@ManyToMany
	@JoinTable(name = "game_team", joinColumns = @JoinColumn(name = "game_id"), inverseJoinColumns = @JoinColumn(name = "team_id"))
	private Set<Team> teams;

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

	public Set<Team> getTeams() {
		return teams.stream().map(GameTeam::getTeam).collect(Collectors.toSet());
	}

	public void setTeams(Set<Team> teams) {
		this.teams = teams.stream().map(t -> new GameTeam(this, t)).collect(Collectors.toSet());
	}

	public GameState getState() {
		return state;
	}

	public void setState(GameState state) {
		this.state = state;
	}

	public void advanceState(GameState state) throws GameException {
		// TODO - check if transition is allowed 
		this.state = state;
	}

	@Override
	public boolean isNew() {
		// TODO Auto-generated method stub
		return false;
	}
}
