package at.timeguess.backend.api.services;

import org.springframework.stereotype.Service;

import at.timeguess.backend.model.CubeFace;

@Service
public class CubeService {

	private CubeFace cubeFace;

	public CubeFace addCubeFace(CubeFace cubeFace) {
		this.cubeFace = new CubeFace();
		this.cubeFace.setActivity(cubeFace.getActivity());
		this.cubeFace.setPoints(cubeFace.getPoints());
		this.cubeFace.setTime(cubeFace.getTime());
		return this.cubeFace;
	}

	public CubeFace getCubeFace() {
		return this.cubeFace;
	}

	public CubeFace updateCubeFace(CubeFace cubeFace) {
		
		if(cubeFace.getActivity() != null)
			this.cubeFace.setActivity(cubeFace.getActivity());
		
		if(cubeFace.getPoints() != null)
			this.cubeFace.setPoints(cubeFace.getPoints());
		
		if(cubeFace.getTime() != null)
			this.cubeFace.setTime(cubeFace.getTime());
		
		return this.cubeFace;
	}
	
	
	
}
