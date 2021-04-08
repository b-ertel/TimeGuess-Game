package at.timeguess.backend.repositories;

import at.timeguess.backend.model.Cube;

public interface CubeRepository extends AbstractRepository<Cube, Long> {
	
	Cube findById(Long id);

	Cube findByMacAddress(String macAddress);
    
}
