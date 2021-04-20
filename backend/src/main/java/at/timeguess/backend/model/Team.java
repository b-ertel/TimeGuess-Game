package at.timeguess.backend.model;

import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.ConstraintMode;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
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

    @ManyToMany(fetch = FetchType.EAGER, cascade = { CascadeType.DETACH, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.PERSIST }, targetEntity = User.class)
    @JoinTable(name = "team_user",
            joinColumns = @JoinColumn(name = "team_id", nullable = false, updatable = false),
            inverseJoinColumns = @JoinColumn(name = "user_id", nullable = false, updatable = false),
            foreignKey = @ForeignKey(ConstraintMode.CONSTRAINT),
            inverseForeignKey = @ForeignKey(ConstraintMode.CONSTRAINT))
   private Set<User> teamMembers;

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

	public Set<User> getTeamMembers() {
		return teamMembers;
	}

	public void setTeamMembers(Set<User> teamMembers) {
		this.teamMembers = teamMembers;
	}
    
}
