package at.timeguess.backend.model;

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

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

    @Column(name = "name", unique = true)
    private String name;

    @OneToMany(mappedBy = "topic", cascade = CascadeType.REMOVE)
    private Set<Term> terms;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public Long getId() {
        return this.id;
    }

    public Set<Term> getTerms() {
        return terms;
    }

    public void setTerms(Set<Term> terms) {
        this.terms = terms;
    }

    @Override
    public boolean isNew() {
        return this.id == null || this.id == 0L;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof Topic)) {
            return false;
        }
        final Topic other = (Topic) obj;
        if (!Objects.equals(this.name, other.name)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        return (int) Objects.hashCode(this.name) + 42;
    }

    @Override
    public int compareTo(Topic o) {
        return this.name.compareTo(o.getName());
    }
}
