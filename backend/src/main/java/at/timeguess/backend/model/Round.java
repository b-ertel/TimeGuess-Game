package at.timeguess.backend.model;

import java.io.Serializable;
import java.util.Objects;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;
import javax.persistence.Transient;

import org.springframework.data.domain.Persistable;

/**
 * Entity representing a round.
 */
@Entity
public class Round implements Serializable, Persistable<RoundId> {

    private static final long serialVersionUID = -1284766274669269322L;

    @EmbeddedId
    private RoundId id;

    @Transient
    private Activity activity;

    @ManyToOne
    @JoinColumn(name = "guessing_User", nullable = false)
    private User guessingUser;

    @ManyToOne
    @JoinColumn(name = "guessing_Team", nullable = false)
    private Team guessingTeam;

    @Transient
    private Set<Team> verifyingTeams;

    @Column(nullable = false)
    private boolean correctAnswer = false;

    private int points = 0;

    @ManyToOne
    @JoinColumn(name = "termId", nullable = false)
    private Term termToGuess;

    @ManyToOne
    @MapsId("gameId")
    private Game game;

    public Round() {
        this.id = new RoundId();
    }

    public Round(Game game, Integer nr) {
        this.id = new RoundId(game.getId(), nr);
        this.game = game;
    }

    @Transient
    private int time;

    @Override
    public RoundId getId() {
        return this.id;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public int getPoints() {
        return this.points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public int getNr() {
        return id.getNr();
    }

    public void setNr(int nr) {
        this.id.setNr(nr);
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
        this.id.setGameId(game.getId());
    }

    public Activity getActivity() {
        return activity;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    @Override
    public int hashCode() {
        final int prime = 7;
        int result = 7;
        result = prime * result + (id == null ? 0 : id.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;

        if (obj == null || getClass() != obj.getClass()) { return false; }

        final Round other = (Round) obj;
        return Objects.equals(getId(), other.getId());
    }

    @Override
    public boolean isNew() {
        return this.id == null || this.id.getNr() == 0;
    }
}
