package at.timeguess.backend.model;

import java.io.Serializable;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

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

import org.springframework.data.domain.Persistable;

/**
 * Entity representing a team.
 */
@Entity
public class Team implements Serializable, Comparable<Team>, Persistable<Long> {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 100, nullable = false, unique = true)
    private String name;

    @OneToMany(mappedBy = "team")
    private Set<GameTeam> games;

    @ManyToMany(fetch = FetchType.EAGER,
            cascade = { CascadeType.DETACH, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.PERSIST },
            targetEntity = User.class)
    @JoinTable(name = "team_user",
            joinColumns = @JoinColumn(name = "team_id", nullable = false, updatable = false),
            inverseJoinColumns = @JoinColumn(name = "user_id", nullable = false, updatable = false),
            foreignKey = @ForeignKey(ConstraintMode.CONSTRAINT),
            inverseForeignKey = @ForeignKey(ConstraintMode.CONSTRAINT))
    private Set<User> teamMembers;

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

    public Set<Game> getGames() {
        return games == null ? null : games.stream().map(GameTeam::getGame).collect(Collectors.toSet());
    }

    public void setGames(Set<Game> games) {
        this.games = games == null ? null : games.stream().map(g -> new GameTeam(g, this)).collect(Collectors.toSet());
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

    @Override
    public int compareTo(Team o) {
        return name.compareTo(o.getName());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;

        if (obj == null || getClass() != obj.getClass()) return false;

        final Team o2 = (Team) obj;
        return Objects.equals(getId(), o2.getId());
    }

    @Override
    public int hashCode() {
        final int prime = 7;
        int result = 27;
        result = prime * result + (id == null ? 0 : id.hashCode());
        return result;
    }

    @Override
    public String toString() {
        return String.format("%s[id=%d, name=%s]", getClass().getSimpleName(), id, name);
    }

    @Override
    public boolean isNew() {
        return this.id == null || this.id == 0L;
    }
}
