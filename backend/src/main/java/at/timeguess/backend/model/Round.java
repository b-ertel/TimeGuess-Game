package at.timeguess.backend.model;

import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Transient;

import org.springframework.data.domain.Persistable;

@Entity
@SequenceGenerator(name="seq", initialValue=30, allocationSize=100)
public class Round implements Persistable<Long> {

	@Id
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="seq")
	private Long id;
	
	@Transient
	private int nr;
	
	@ManyToOne
	@JoinColumn(name="guessing_User", nullable=false)
	private User guessingUser;
	
	@ManyToOne
	@JoinColumn(name="guessing_Team", nullable=false)
	private Team guessingTeam;
	
	@Transient
	private Set<Team> verifyingTeams;

	@Column(nullable = false)
	private boolean correctAnswer = false;
	
	private int points = 0;

	@ManyToOne
	@JoinColumn(name="termId", nullable=false)
	private Term termToGuess;

	@ManyToOne
	@JoinColumn(name="gameId", nullable=false)
	private Game game;
	
	public int getPoints() {
		return this.points;
	}
	
	public void setPoints(int points) {
		this.points = points;
	}
	
	public int getNr() {
		return nr;
	}

	public void setNr(int nr) {
		this.nr = nr;
	}

	public User getGuessingUser() {
		return guessingUser;
	}

	public void setGuessingUser(User guessingUser) {
		this.guessingUser = guessingUser;
	}

	public Team getGuessingTeam() {
		return guessingTeam;
	}

	public void setGuessingTeam(Team guessingTeam) {
		this.guessingTeam = guessingTeam;
	}

	public boolean isCorrectAnswer() {
		return correctAnswer;
	}

	public void setCorrectAnswer(boolean correctAnswer) {
		this.correctAnswer = correctAnswer;
	}
	
	public Term getTermToGuess() {
		return termToGuess;
	}

	public void setTermToGuess(Term termToGuess) {
		this.termToGuess = termToGuess;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Set<Team> getVerifyingTeams() {
		return verifyingTeams;
	}

	public void setVerifyingTeams(Set<Team> verifyingTeams) {
		this.verifyingTeams = verifyingTeams;
	}

	public Game getGame() {
		return game;
	}

	public void setGame(Game game) {
		this.game = game;
	}

	@Override
	public Long getId() {
		// TODO Auto-generated method stub
		return this.id;
	}

	@Override
	public boolean isNew() {
		// TODO Auto-generated method stub
		return false;
	}

}
