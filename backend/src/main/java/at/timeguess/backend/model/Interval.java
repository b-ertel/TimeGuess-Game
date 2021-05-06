package at.timeguess.backend.model;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Entity representing an interval for TimeFlip devices.
 */
@Entity
@Table(name="INTERVAL_TABLE")
public class Interval implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Enumerated(EnumType.STRING)
    private IntervalType type;

    private int value;

    public IntervalType getType() {
        return type;
    }

    public void setType(IntervalType type) {
        this.type = type;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

}
