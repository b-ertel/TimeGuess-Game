package at.timeguess.backend.repositories;

import java.util.Optional;

import at.timeguess.backend.model.Configuration;

public interface ConfigurationRepository extends AbstractRepository<Configuration, Long> {

    public Optional<Configuration> findById(Long id);

    public Configuration findByCube(Long id);

    public Configuration findByCubeface(Long id);

}
