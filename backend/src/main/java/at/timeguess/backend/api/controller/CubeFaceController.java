package at.timeguess.backend.api.controller;

import java.util.List;

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
	
	private CubeFace cubeFace;
	
	@PostMapping("/dice")
	public CubeFace createCubeFace(@RequestBody CubeFace cubeFace) {
		this.cubeFace = new CubeFace();
	//	this.cubeFace.setActivity(cubeFace.getActivity());
	//	this.cubeFace.setPoints(cubeFace.getPoints());
	//	this.cubeFace.setTime(cubeFace.getTime());
		this.cubeFace.setId(cubeFace.getId());
	//	this.cubeFace.setCube(this.cube);
	//	cubeRepo.save(cubeFace);
		return cubeService.addCubeFace(cubeFace);
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
	
	
	
//	@GetMapping("/dice/{id}")
//	private CubeFace getCubeFace(@PathVariable Long id) {
//		return cubeService.getOnCubeFace(id);
//	}
	
//	@PatchMapping("/dice/{id}")
//	private CubeFace updateCubeFace(@PathVariable Long id, @RequestBody CubeFace cubeFace) {    //there is only one cubeFace relevant for the game, passed faces are not saved
//		return cubeService.updateCubeFace(id, cubeFace);
//	}
	

}


