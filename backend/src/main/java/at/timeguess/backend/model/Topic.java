package at.timeguess.backend.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;

import org.springframework.data.domain.Persistable;

/**
 * Entity representing Topics.
 */
@Entity
@SequenceGenerator(name="seq", initialValue=30, allocationSize=100)
public class Topic implements Persistable<Long> {

    @Id
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="seq")
	private Long id;

    @Column(name="name", unique=true)
    private String name;

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

	@Override
	public boolean isNew() {
		return false;
	}
    
}
