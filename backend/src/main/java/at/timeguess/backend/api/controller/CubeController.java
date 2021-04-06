package at.timeguess.backend.api.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import at.timeguess.backend.api.services.CubeFaceService;
import at.timeguess.backend.api.services.CubeService;
import at.timeguess.backend.model.Cube;
import at.timeguess.backend.model.CubeFace;

@RestController
public class CubeController {
	
	@Autowired
	private CubeService cubeService;
	
	private Cube cube;
	
	@PostMapping("/cube")
	public Cube createCube(@RequestBody Cube cube) {
		this.cube = new Cube();
		this.cube.setId(cube.getId());
	//	this.cube.setDeviceNo(cube.getDeviceNo());
		return cubeService.addCube(this.cube);
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
	

}


