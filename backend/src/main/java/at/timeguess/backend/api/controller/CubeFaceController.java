package at.timeguess.backend.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import at.timeguess.backend.api.services.CubeService;
import at.timeguess.backend.model.CubeFace;

@RestController
public class CubeFaceController {
	
	@Autowired
	private CubeService cubeService;
	
	@PostMapping("/dice")
	public CubeFace createCubeFace(@RequestBody CubeFace cubeFace) {
		return cubeService.addCubeFace(cubeFace);
	}
	
	@GetMapping("/dice/{id}")
	private CubeFace getCubeFace(@PathVariable Long id) {
		return cubeService.getOnCubeFace(id);
	}
	
	@PatchMapping("/dice/{id}")
	private CubeFace updateCubeFace(@PathVariable Long id, @RequestBody CubeFace cubeFace) {    //there is only one cubeFace relevant for the game, passed faces are not saved
		return cubeService.updateCubeFace(id, cubeFace);
	}
	

}


