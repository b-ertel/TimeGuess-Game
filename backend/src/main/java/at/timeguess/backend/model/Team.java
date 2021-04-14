package at.timeguess.backend.model;

import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;

@Entity
public class Team implements  Comparable<Team> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String name;

    @OneToMany(mappedBy = "team")
    private Set<GameTeam> games;

    @ManyToMany(mappedBy = "teams")
    private Set<User> teamMembers;

    private TeamState state;

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

    public Set<GameTeam> getGames() {
        return games;
    }

    public void setGames(Set<GameTeam> games) {
        this.games = games;
    }

    /**
     * @return the teamMembers
     */
    public Set<User> getTeamMembers() {
        return teamMembers;
    }

    /**
     * @param teamMembers the teamMembers to set
     */
    public void setTeamMembers(Set<User> teamMembers) {
        this.teamMembers = teamMembers;
    }

    public TeamState getState() {
        return state;
    }

    public void setState(TeamState state) {
        this.state = state;
    }

    @Override
    public int compareTo(Team o) {
        return name.compareTo(o.getName());
    }
}
