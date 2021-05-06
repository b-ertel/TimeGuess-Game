package at.timeguess.backend.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.util.Objects;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.springframework.data.domain.Persistable;

/**
 * Entity representing Terms.
 */
@Table(uniqueConstraints = @UniqueConstraint(columnNames={"topic", "name"}))
@Entity
@SequenceGenerator(name = "seq", initialValue = 30, allocationSize = 100)
public class Term implements Persistable<Long>, Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 250, nullable = false)
    private String name;

    @ManyToOne
    @JoinColumn(name = "topic", nullable = false)
    private Topic topic;

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

    public Topic getTopic() {
        return topic;
    }

    public void setTopic(Topic topic) {
        this.topic = topic;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;

        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        final Term other = (Term) obj;
        return Objects.equals(getId(), other.getId());
    }

    @Override
    public int hashCode() {
        final int prime = 7;
        int result = 13;
        result = prime * result + (this.id == null ? 0 : this.id.hashCode());
        return result;
    }

    @Override
    public boolean isNew() {
        return this.id == null || this.id == 0L;
    }
}
