package at.timeguess.backend.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import at.timeguess.backend.model.CubeFace;
import at.timeguess.backend.repositories.CubeFaceRepository;

/**
 * a service to manage {@link CubeFace} entities
 *
 */
@Component
@Scope("application")
public class CubeFaceService {

	@Autowired
	CubeFaceRepository cubeFaceRepo;
	
	/**
	 * @return a list of all CubeFace entities
	 */
	public List<CubeFace> allCubeFaces(){
		return cubeFaceRepo.findAll();
	}
	
}
