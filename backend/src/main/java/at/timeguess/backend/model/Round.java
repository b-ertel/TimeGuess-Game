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
	@JoinColumn(name="userId", nullable=false)
	private User guessingUser;
	
	//@Column(nullable = false)
	//private Team guessingTeam;
	
	//@Transient
	//private Set<Team> verifyingTeams;

	@Column(nullable = false)
	private boolean correctAnswer = false;
/*	
	@ManyToOne
	@JoinColumn(name="termId", nullable=false)
	private Term termToGuess;
*/	
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
/*
	public Team getGuessingTeam() {
		return guessingTeam;
	}

	public void setGuessingTeam(Team guessingTeam) {
		this.guessingTeam = guessingTeam;
	}
*/
	public boolean isCorrectAnswer() {
		return correctAnswer;
	}

	public void setCorrectAnswer(boolean correctAnswer) {
		this.correctAnswer = correctAnswer;
	}

	public void setId(Long id) {
		this.id = id;
	}
/*	
	public Set<Team> getVerifyingTeams() {
		return verifyingTeams;
	}

	public void setVerifyingTeams(Set<Team> verifyingTeams) {
		this.verifyingTeams = verifyingTeams;
	}
*/	

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
