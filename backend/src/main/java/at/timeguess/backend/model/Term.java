package at.timeguess.backend.model;

import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Transient;

import org.springframework.data.domain.Persistable;

import at.timeguess.backend.model.Topic;

/**
 * Entity representing Terms.
 */
@Entity
@SequenceGenerator(name="seq", initialValue=30, allocationSize=100)
public class Term implements Persistable<Long> {

    @Id
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="seq")
	private Long id;

    private String name;

    @ManyToOne
    @JoinColumn(name="topic", nullable=false)
    private Topic topic;

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
	public Long getId() {
		return this.id;
	}

	@Override
	public boolean isNew() {
		return false;
	}
    
}
