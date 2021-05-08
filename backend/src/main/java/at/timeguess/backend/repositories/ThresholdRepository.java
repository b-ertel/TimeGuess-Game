package at.timeguess.backend.repositories;

import at.timeguess.backend.model.Threshold;
import at.timeguess.backend.model.ThresholdType;

/**
 * A repository for entities of type {@link Threshold}.
 */
public interface ThresholdRepository extends AbstractRepository<Threshold, ThresholdType> {

    Threshold findByType(ThresholdType type);

}
