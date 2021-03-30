package at.timeguess.backend.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import at.timeguess.backend.model.CubeFace;
import at.timeguess.backend.model.Team;

@RestController
public class CubeFaceController {
	
	@Autowired
	private CubeService cubeService;
	
	@PostMapping("/dice")
	public CubeFace createCubeFace(@RequestBody CubeFace cubeFace) {
		return cubeService.addCubeFace(cubeFace);
	}
	
	@GetMapping("/dice")
	private CubeFace getCubeFace() {
		return cubeService.getCubeFace();
	}
	
	@PatchMapping("/dice")
	private CubeFace updateCubeFace(/*@PathVariable Long id, */@RequestBody CubeFace cubeFace) {
		return cubeService.updateCubeFace(cubeFace);
	}
	

}
