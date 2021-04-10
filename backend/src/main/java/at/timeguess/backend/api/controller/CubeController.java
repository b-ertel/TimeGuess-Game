package at.timeguess.backend.api.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import at.timeguess.backend.services.CubeService;
import at.timeguess.backend.model.Cube;

import at.timeguess.backend.model.api.OnboardingMessage;
import at.timeguess.backend.model.api.OnboardingResponse;
import at.timeguess.backend.model.api.HealthMessage;
import at.timeguess.backend.model.api.HealthResponse;
import at.timeguess.backend.model.api.FacetsMessage;
import at.timeguess.backend.model.api.FacetsResponse;

/**
 *  api controller which handles httpRequests of new {@link Cube} entities 
 *
 */

@RestController
public class CubeController {
	
	@Autowired
	private CubeService cubeService;
	
	private Cube cube;

    /**
     * Process messages from a Raspberry pi signaling
     * successful startup and connection with a TimeFlip device.
     * 
     * @param message the message
     * @return the response
     */
    @PostMapping("/api/onboarding")
    private OnboardingResponse processOnboarding(@RequestBody OnboardingMessage message) {
        // just a placeholder for the moment
        // "real" processing of the message and generation of response should happen in a service
        OnboardingResponse response = new OnboardingResponse();
        response.setSuccess(true);
        return response;
    }

    /**
     * Process messages from a TimeFlip device signaling
     * a change of the facets characteristic.
     * 
     * @param message the message
     * @return the response
     */
    @PostMapping("/api/facets")
    private FacetsResponse processFacets(@RequestBody FacetsMessage message) {
        // do something ...
        FacetsResponse response = new FacetsResponse();
        response.setSuccess(true);
        response.setConfiguration(0);
        return response;
    }

    /**
     * Process messages from a Raspberry Pi signaling
     * general availability and containing information on battery
     * level and signal strength of the TimeFlip device.
     * 
     * @param message the message
     * @return the response
     */
    @PostMapping("/api/health")
    private HealthResponse processHealth(@RequestBody HealthMessage message) {
        // just a placeholder for the moment
        // "real" processing of the message and generation of response should happen in a service
        HealthResponse response = new HealthResponse();
        response.setSuccess(true);
        return response;
    }
    
	/**
	 * @param cube to register -> i.e. which has to be saved in the database
	 * @return registered Cube
	 */
    @PreAuthorize("hasAuthority('ADMIN')")
	public Cube registerCube(Cube cube) {
		
		this.cube = new Cube();
		this.cube.setId(cube.getId());
		this.cube.setMacAddress(cube.getMacAddress());
		this.cube.setName(cube.getName());
		saveCube();
		
		return this.cube;
		
	}
	
	public Cube getCube() {
		return this.cube;
	}
	
	public void setCube(Cube cube) {
		this.cube = cube;
	}
	
	public void saveCube() {
		cubeService.saveCube(this.cube);
	}
	
	public List<Cube> getAllCubes() {
		return cubeService.getAllCubes();
	}
	
	public boolean isMacAddressKnown(Cube cube){
		return cubeService.isMacAddressKnown(cube);
	}

}

