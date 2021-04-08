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
	public boolean isMacAddressKnown(String macAddress) {
		if(cubeRepo.findByMacAddress(macAddress)==null){
			return false;
		}
		else {
			return true;
		}
	}
    
    /**
     * to find out if cube there exists a configuration for the given Cube
     * 
     * @param cube the cube to find out if it is configured
     * @return true if is configured, otherwise false
     */
    public boolean isConfigured(Cube cube) {
    	
    	if(cubeRepo.findById(cube.getId()).getIsConfigured()) {
    		return true;
    	}
    	
    	return false;
    }
    
    public Cube findCubeById(Long id) {
    	return cubeRepo.findById(id);
    }
    
    public Cube findBueByMacAddress(String mac) {
    	return cubeRepo.findByMacAddress(mac);
    }

	
	
}
