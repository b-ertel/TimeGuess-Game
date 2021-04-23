package at.timeguess.backend.model;

import java.util.Objects;
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
public class Team implements Comparable<Team> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String name;

    @OneToMany(mappedBy = "team")
    private Set<GameTeam> games;

    @ManyToMany(fetch = FetchType.EAGER, cascade = { CascadeType.DETACH, CascadeType.MERGE, CascadeType.REFRESH,
            CascadeType.PERSIST }, targetEntity = User.class)
    @JoinTable(name = "team_user", joinColumns = @JoinColumn(name = "team_id", nullable = false, updatable = false), inverseJoinColumns = @JoinColumn(name = "user_id", nullable = false, updatable = false), foreignKey = @ForeignKey(ConstraintMode.CONSTRAINT), inverseForeignKey = @ForeignKey(ConstraintMode.CONSTRAINT))
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

    @Override
    public String toString() {
        return String.format("%s[id=%d]", getClass().getSimpleName(), getId());
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || getClass() != obj.getClass())
            return false;
        if (this == obj)
            return true;
        Team o2 = (Team) obj;
        return Objects.equals(getId(),o2.getId()) && Objects.equals(getName(), o2.getName());
    }

    @Override
    public int hashCode() {
        return (getId() != null) ? (getClass().getSimpleName().hashCode() + getId().hashCode()) : super.hashCode();
    }
}
