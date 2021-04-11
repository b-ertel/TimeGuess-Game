package at.timeguess.backend.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import at.timeguess.backend.model.Cube;
import at.timeguess.backend.model.CubeStatus;
import at.timeguess.backend.model.api.OnboardingMessage;
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
	 * method to create a new entry for a cube device in the database
	 * 
	 * @param message from the new Cube which contains attribtus for the new Cube
	 * @return new Cube
	 */
	public Cube createCube(OnboardingMessage message) {
		Cube newCube = new Cube();
		newCube.setMacAddress(message.getIdentifier());
		newCube.setConfiguration(message.getCalibrationVersion());  
		
		return newCube;
	}
	
	/**
	 * method to check whether Cube is known and if it's configured and sets corresponding CubeStatus
	 * 
	 * @param message from the timeflip device from online cube
	 * @return updated cube
	 */
	public Cube updateCube(OnboardingMessage message) {
		
		Cube updatedCube = new Cube();
		
		if(isMacAddressKnown(message.getIdentifier())) {						// Cube is already in database
			updatedCube = cubeRepo.findByMacAddress(message.getIdentifier());
		}
		else {
			updatedCube = createCube(message);									// Cube is new and has to be created
		}
		
		if(updatedCube.getConfiguration() == message.getCalibrationVersion()
				&& message.getCalibrationVersion() != 0){								// Cube is configured and ready
			updatedCube.setCubeStatus(CubeStatus.READY);
		}
		else { 
			updatedCube.setCubeStatus(CubeStatus.LIVE);							// Cube lost his configuration or has not been configured yet
			updatedCube.setConfiguration(0);
		}
		
		saveCube(updatedCube);
		return updatedCube;
	}


	/**
	 * @return a list of all cubes 
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
		if(cubeRepo.findByMacAddress(macAddress).getMacAddress()==null){
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
    	
    	if(cubeRepo.findById(cube.getId()).get().getConfiguration()!=0) {
    		return true;
    	}
    	return false;
    }
    
	
}
