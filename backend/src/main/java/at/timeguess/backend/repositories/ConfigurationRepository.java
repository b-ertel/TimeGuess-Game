package at.timeguess.backend.repositories;

import java.util.List;

import at.timeguess.backend.model.Configuration;

public interface ConfigurationRepository extends AbstractRepository<Configuration, Long> {

	public Configuration findById();
	
	public Configuration findByCubeId();

	public Configuration findByCubeFaceId();
	
	public Configuration findByMacAddress();
}
