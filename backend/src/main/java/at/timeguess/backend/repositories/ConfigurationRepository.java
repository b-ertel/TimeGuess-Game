package at.timeguess.backend.repositories;

import java.util.List;

import at.timeguess.backend.model.Configuration;

public interface ConfigurationRepository extends AbstractRepository<Configuration, Long> {

	public Configuration findById(Long id);
	
	public Configuration findByCube(Long id);

	public Configuration findByCubeface(Long id);
	
	public Configuration findByFacet(Integer facet);
}
