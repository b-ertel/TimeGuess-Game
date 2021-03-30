package at.timeguess.backend.model;

import java.io.Serializable;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;

import org.springframework.data.domain.Persistable;

import at.timeguess.backend.model.game.GameException;
import at.timeguess.backend.model.game.GameState;

@Entity
public class Game implements Persistable<Long>, Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 7796168379870118060L;

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
        return teams;
    }

    public void setTeams(Set<Team> teams) {
        this.teams = teams;
    }

    /**
     * @return the topic
     */
    public Topic getTopic() {
        return topic;
    }

    /**
     * @param topic the topic to set
     */
    public void setTopic(Topic topic) {
        this.topic = topic;
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

    public boolean canBePlayed() {
        // TODO
        // check for number teams, teams sufficient players
        return false;
    }

    @Override
    public boolean isNew() {
        // TODO Auto-generated method stub
        return false;
    }
}
