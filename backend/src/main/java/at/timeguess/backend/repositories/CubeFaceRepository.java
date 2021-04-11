package at.timeguess.backend.repositories;

import java.util.List;

import at.timeguess.backend.model.CubeFace;

public interface CubeFaceRepository extends AbstractRepository<CubeFace, String> {
	
	CubeFace findById(String id);
    
}
