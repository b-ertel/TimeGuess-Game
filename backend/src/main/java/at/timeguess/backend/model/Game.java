package at.timeguess.backend.model;

import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.*;

import org.springframework.data.domain.Persistable;

@Entity
public class Game implements Serializable, Persistable<Long> {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String name;

    private int maxPoints;

    private GameState status; // more finegrained replacement for status

    private int roundNr;

    // NOTE: need this to implement deletion! Otherwise will get data integrity
    // exceptions.
    //
    // TODO derive roundNr from this list to be consistent.
    //
    // TODO works only with FetchType.EAGER - otherwise gives
    //      `org.hibernate.LazyInitializationException: failed to lazily initialize a collection of role: at.timeguess.backend.model.Game.rounds, could not initialize proxy - no Session`
    //      see https://stackoverflow.com/questions/22821695/how-to-fix-hibernate-lazyinitializationexception-failed-to-lazily-initialize-a
    //      are there alternatives?
    //      `Hibernate.initialize(game);` does not work
    @OneToMany(mappedBy = "game", cascade = { CascadeType.ALL }, orphanRemoval = true, fetch = FetchType.EAGER)
    private Set<Round> rounds = new HashSet<>();

    @OneToMany(mappedBy = "game", cascade = { CascadeType.ALL }, orphanRemoval = true, fetch = FetchType.EAGER)
    private Set<GameTeam> teams = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY, cascade = { CascadeType.DETACH, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.PERSIST }, targetEntity = User.class)
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

    public Set<GameTeam> getTeams() {
        return teams;
    }

    public void setTeams(Set<GameTeam> teams) {
        this.teams = teams;
    }

    // NOTE for the editing its convenient to have the Teams directly
    public List<Team> getActualTeams(){
        return teams.stream().map(gt -> gt.getTeam()).collect(Collectors.toList());
    }

    public void setActualTeams(List<Team> newTeams){
        teams = teams.stream().filter(t -> newTeams.contains(t.getTeam())).collect(Collectors.toSet());
        
        newTeams.removeAll(getActualTeams());
        // from here newTeams only contains teams not already joined
        List<GameTeam> newGameTeams = newTeams.stream().map(t -> new GameTeam(this, t)).collect(Collectors.toList());
        teams.addAll(newGameTeams);
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

    @Override
    public int hashCode() {
        final int prime = 7;
        int result = 17;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((topic == null) ? 0 : topic.hashCode());
        result = prime * result + ((creator == null) ? 0 : creator.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        final Game o2 = (Game) obj;

        return Objects.equals(id, o2.getId())
                && Objects.equals(name, o2.getName())
                && Objects.equals(topic, o2.getTopic())
                && Objects.equals(creator, o2.getCreator());
    }

    public Set<User> getConfirmedUsers() {
        return confirmedUsers;
    }

    public void setConfirmedUsers(Set<User> confirmedUsers) {
        this.confirmedUsers = confirmedUsers;
    }

    @Override
    public boolean isNew() {
        return this.id == null || this.id == 0L;
    }
}
