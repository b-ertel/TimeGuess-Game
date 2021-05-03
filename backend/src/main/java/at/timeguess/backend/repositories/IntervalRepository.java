package at.timeguess.backend.repositories;

import at.timeguess.backend.model.Interval;
import at.timeguess.backend.model.IntervalType;

/**
 * A repository for entities of type {@link Interval}.
 */
public interface IntervalRepository extends AbstractRepository<Interval, IntervalType> {

    Interval findByType(IntervalType type);

}
