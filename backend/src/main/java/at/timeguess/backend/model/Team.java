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
public class Team {

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

    public TeamState getState() {
        return state;
    }

    public void setState(TeamState state) {
        this.state = state;
    }
}
