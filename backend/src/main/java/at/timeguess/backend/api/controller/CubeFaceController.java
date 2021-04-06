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
import at.timeguess.backend.model.Cube;
import at.timeguess.backend.model.CubeFace;

@RestController
public class CubeFaceController {
	
	@Autowired
	private CubeFaceService cubeService;
	
	private CubeFace cubeFace;
	private Cube cube;
	
	@PostMapping("/cubeFace")
	public CubeFace createCubeFace(@RequestBody CubeFace cubeFace) {
		this.cubeFace = new CubeFace();
	//	this.cubeFace.setActivity(cubeFace.getActivity());
	//	this.cubeFace.setPoints(cubeFace.getPoints());
	//	this.cubeFace.setTime(cubeFace.getTime());
		this.cubeFace.setId(cubeFace.getId());
		this.cubeFace.setCube(cube);
	//	this.cubeFace.setCube(this.cube);
	//	cubeRepo.save(cubeFace);
		return cubeService.addCubeFace(this.cubeFace);
	}
	
	public CubeFace getCubeFace() {
		return this.cubeFace;
	}
	
	public void setCubeFace(CubeFace cubeFace) {
		this.cubeFace = cubeFace;
	}
	
	public void saveCubeFace() {
		cubeService.saveCubeFace(this.cubeFace);
	}
	
	public List<CubeFace> getAllCubeFaces() {
		return cubeService.getAllCubeFaces();
	}
	
	public void setCube(Cube cube) {
		this.cube = cube;
	}

	public Cube getCube() {
		return cube;
	}
	
	
	
	
	
//	@GetMapping("/dice/{id}")
//	private CubeFace getCubeFace(@PathVariable Long id) {
//		return cubeService.getOnCubeFace(id);
//	}
	
//	@PatchMapping("/dice/{id}")
//	private CubeFace updateCubeFace(@PathVariable Long id, @RequestBody CubeFace cubeFace) {    //there is only one cubeFace relevant for the game, passed faces are not saved
//		return cubeService.updateCubeFace(id, cubeFace);
//	}
	

}


