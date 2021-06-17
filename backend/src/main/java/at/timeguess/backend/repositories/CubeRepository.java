package at.timeguess.backend.repositories;

import at.timeguess.backend.model.Cube;

/**
 * Repository for managing {@link Cube} entities.
 */
public interface CubeRepository extends AbstractRepository<Cube, Long> {

    Cube findByMacAddress(String macAddress);

}
