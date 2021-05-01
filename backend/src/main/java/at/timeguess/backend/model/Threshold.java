package at.timeguess.backend.model;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Entity representing a threshold for TimeFlip devices.
 */
@Entity
@Table(name="THRESHOLD_TABLE")
public class Threshold implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Enumerated(EnumType.STRING)
    private ThresholdType type;

    private int value;

    public ThresholdType getType() {
        return type;
    }

    public void setType(ThresholdType type) {
        this.type = type;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

}
