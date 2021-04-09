package at.timeguess.backend.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import at.timeguess.backend.model.Cube;
import at.timeguess.backend.repositories.CubeRepository;

/**
 * a service to handle {@link Cube} entities
 *
 */
@Service
public class CubeService {

	@Autowired
	private CubeRepository cubeRepo;

	/**
	 * @param cube: the cube to save
	 */
	public void saveCube(Cube cube) {
		cubeRepo.save(cube);
	}


	/**
	 * @return a list of cubes 
	 */
	public List<Cube> getAllCubes() {
		return cubeRepo.findAll();
	}

	/**
	 * @param cube to find out if mac address is already known
	 * @return true if mac address is known, false otherwise
	 */
    @PreAuthorize("hasAuthority('ADMIN')")
	public boolean isMacAddressKnown(Cube cube) {
		if(cubeRepo.findByMacAddress(cube.getMacAddress())==null){
			return false;
		}
		else {
			return true;
		}
	}




	
	
	
}
