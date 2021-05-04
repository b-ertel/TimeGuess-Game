package at.timeguess.backend.model;

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import javax.persistence.SequenceGenerator;

import org.springframework.data.domain.Persistable;

/**
 * Entity representing Topics.
 */
@Entity
@SequenceGenerator(name = "seq", initialValue = 30, allocationSize = 100)
public class Topic implements Comparable<Topic>, Persistable<Long>, Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 100, nullable = false, unique = true)
    private String name;

    private boolean enabled;

    @OneToMany(mappedBy = "topic", cascade = CascadeType.REMOVE)
    private Set<Term> terms;

    @Override
    public Long getId() {
        return this.id;
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

    public boolean isEnabled() {
        return enabled;
    }

    public void enable() {
        this.enabled = true;
    }

    public void disable() {
        this.enabled = false;
    }

    public Set<Term> getTerms() {
        return terms == null ? new HashSet<>() : terms;
    }

    public void setTerms(Set<Term> terms) {
        this.terms = terms;
    }

    @Override
    public int compareTo(Topic o) {
        return this.name.compareTo(o.getName());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;

        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        final Topic other = (Topic) obj;
        return Objects.equals(getId(), other.getId());
    }

    @Override
    public int hashCode() {
        final int prime = 11;
        int result = 42;
        result = prime * result + (this.id == null ? 0 : this.id.hashCode());
        return result;
    }

    @Override
    public boolean isNew() {
        return this.id == null || this.id == 0L;
    }
}
