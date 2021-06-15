package at.timeguess.backend.model;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.*;

import org.springframework.data.domain.Persistable;

/**
 * Entity representing a game.
 */
@Entity
public class Game implements Serializable, Persistable<Long> {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 100, nullable = false, unique = true)
    private String name;

    private int maxPoints;

    private GameState status; // more fine grained replacement for status

    private int roundNr;

    @OneToMany(mappedBy = "game", cascade = { CascadeType.ALL }, orphanRemoval = true, fetch = FetchType.EAGER)
    private Set<Round> rounds = new HashSet<>();

    @OneToMany(mappedBy = "game", cascade = { CascadeType.ALL }, orphanRemoval = true, fetch = FetchType.EAGER)
    private Set<GameTeam> teams = new HashSet<>();

    @ManyToMany(fetch = FetchType.EAGER,
        cascade = { CascadeType.DETACH, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.PERSIST }, targetEntity = User.class)
    @JoinTable(name = "game_user",
        joinColumns = @JoinColumn(name = "game_id", nullable = false, updatable = false),
        inverseJoinColumns = @JoinColumn(name = "user_id", nullable = false, updatable = false),
        foreignKey = @ForeignKey(ConstraintMode.CONSTRAINT),
        inverseForeignKey = @ForeignKey(ConstraintMode.CONSTRAINT))
    private Set<User> confirmedUsers;

    @ManyToOne
    private Topic topic;

    @ManyToOne
    private User creator;

    @ManyToOne
    private Cube cube;

    @Override
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

    /**
     * @return the rounds
     */
    public Set<Round> getRounds() {
        return rounds;
    }

    /**
     * @param rounds the rounds to set
     */
    public void setRounds(Set<Round> rounds) {
        this.rounds = rounds;
    }

    public Set<Team> getTeams() {
        return teams == null ? null : teams.stream().map(GameTeam::getTeam).collect(Collectors.toSet());
    }

    public void setTeams(Set<Team> teams) {
        this.teams = teams == null ? null : teams.stream().map(t -> new GameTeam(this, t)).collect(Collectors.toSet());
    }

    public Topic getTopic() {
        return topic;
    }

    public void setTopic(Topic topic) {
        this.topic = topic;
    }

    /**
     * @return the creator
     */
    public User getCreator() {
        return creator;
    }

    /**
     * @param creator the creator to set
     */
    public void setCreator(User creator) {
        this.creator = creator;
    }

    public Set<User> getConfirmedUsers() {
        return confirmedUsers == null ? new HashSet<>() : confirmedUsers;
    }

    public void setConfirmedUsers(Set<User> confirmedUsers) {
        this.confirmedUsers = confirmedUsers;
    }

    public int getTeamCount() {
        return teams == null ? 0 : teams.size();
    }

    /**
     * @return the cube
     */
    public Cube getCube() {
        return cube;
    }

    /**
     * @param cube the cube to set
     */
    public void setCube(Cube cube) {
        this.cube = cube;
    }

    @Override
    public int hashCode() {
        final int prime = 7;
        int result = 17;
        result = prime * result + (id == null ? 0 : id.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;

        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        final Game other = (Game) obj;
        return Objects.equals(getId(), other.getId());
    }

    @Override
    public boolean isNew() {
        return this.id == null || this.id == 0L;
    }
}
