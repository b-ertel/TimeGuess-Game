package at.timeguess.backend.repositories;

import java.util.List;

import at.timeguess.backend.model.Configuration;
import at.timeguess.backend.model.Cube;

/**
 * Repository for managing {@link Configuration} entities.
 */
public interface ConfigurationRepository extends AbstractRepository<Configuration, Long> {

    /**
     * Find all configurations for a given cube.
     * @param  cube the cube
     * @return the list of configurations
     */
    List<Configuration> findByCube(Cube cube);

}
