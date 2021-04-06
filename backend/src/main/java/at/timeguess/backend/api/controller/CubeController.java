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

import at.timeguess.backend.api.services.CubeService;
import at.timeguess.backend.model.Cube;

/**
 *  api controller which handles httpRequests of new {@link Cube} entities 
 *
 */
@RestController
public class CubeController {
	
	@Autowired
	private CubeService cubeService;
	
	private Cube cube;

	@PostMapping("/cube")
	public Cube createCube(@RequestBody Cube cube) {
		
	/*	if(cube.isConfigured()) {
			// cube is known
		}
		else if(!cube.isConfigured() && isMacAddressKnown(cube)) {
			
			// cube is known and has no configuration (i.e. first time to configure or configuration is lost)
		}
		
		else {
			
			// cube is either configured nor is it known - admin has to register the cube first before configuration is possible
			
		}
		*/
		return registerCube(cube);
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
		this.cube.setStatus("in configuration");
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


