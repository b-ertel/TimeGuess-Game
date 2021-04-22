package at.timeguess.backend.ui.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import at.timeguess.backend.model.Cube;
import at.timeguess.backend.model.CubeFace;
import at.timeguess.backend.services.CubeFaceService;

/**
 * @author a controller to manage {@link CubeFace} un UI 
 *
 */
@Component
@Scope("view")
public class CubeFaceController {
	
	@Autowired
	CubeFaceService cubeFaceService;
	
	private CubeFace cubeFace;
	
	/**
	 * @return a list of all CubeFace entities
	 */
	public List<CubeFace> getAllCubeFaces(){
		return cubeFaceService.allCubeFaces();
	}

	public CubeFace getCubeFace() {
		return this.cubeFace;
	}

	public void setCubeFace(CubeFace cubeface) {
		this.cubeFace = cubeface;
	}
	
}
