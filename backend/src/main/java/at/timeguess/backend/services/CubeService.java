package at.timeguess.backend.services;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import at.timeguess.backend.events.OnboardingEventPublisher;
import at.timeguess.backend.model.Cube;
import at.timeguess.backend.model.api.StatusMessage;
import at.timeguess.backend.repositories.ConfigurationRepository;
import at.timeguess.backend.repositories.CubeRepository;

/**
 * a service to handle {@link Cube} entities
 *
 */
@Service
public class CubeService {

	@Autowired
	OnboardingEventPublisher onboardingEventPuplisher;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(CubeService.class);
	
	@Autowired
	private CubeRepository cubeRepo;
	@Autowired
	private ConfigurationRepository configRepo;

	/**
	 * @param cube: the cube to save
	 */
	public Cube saveCube(Cube cube) {
		return cubeRepo.save(cube);
	}
	
	/**
	 * method to create a new entry for a cube device in the database
	 * 
	 * @param message from the new Cube which contains attributes for the new Cube
	 * @return new Cube
	 */
	public Cube createCube(StatusMessage message) {
		Cube newCube = new Cube();
		newCube.setMacAddress(message.getIdentifier());
		newCube = saveCube(newCube);
		
		LOGGER.info("new Cube createt with mac {}", newCube.getMacAddress());
		
		return newCube;
	}
	
	/**
	 * @return a list of all cubes 
	 */
	public List<Cube> allCubes(){		
		return cubeRepo.findAll();
	}

	/**
	 * @param cube to find out if mac address is already known
	 * @return true if mac address is known, false otherwise
	 */
	public boolean isMacAddressKnown(String macAddress) {
		if(cubeRepo.findByMacAddress(macAddress)==null){
			return false;
		}
		else {
			return true;
		}
	}
    
    /**
     * to find out if there exists a configuration for the given Cube
     * 
     * @param cube the cube to find out if it is configured
     * @return true if is configured, otherwise false
     */
    public boolean isConfigured(Cube cube) {
    	if(configRepo.findByCube(cube)!=null) {
    		return true;
    	}
    	return false;
    }

	public Cube getByMacAddress(String identifier) {
		return cubeRepo.findByMacAddress(identifier);
	}

	public void deleteCube(Cube cube) {
		cubeRepo.delete(cube);
		
	}
    
	
}
