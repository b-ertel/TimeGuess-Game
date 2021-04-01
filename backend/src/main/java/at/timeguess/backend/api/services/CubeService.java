package at.timeguess.backend.api.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import at.timeguess.backend.model.CubeFace;
import at.timeguess.backend.repositories.CubeFaceRepository;

@Service
public class CubeService {

	private CubeFace cubeFace;
	
	@Autowired
	private CubeFaceRepository cubeRepo;

	public CubeFace addCubeFace(CubeFace cubeFace) {
		this.cubeFace = new CubeFace();
		this.cubeFace.setActivity(cubeFace.getActivity());
		this.cubeFace.setPoints(cubeFace.getPoints());
		this.cubeFace.setTime(cubeFace.getTime());
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

	public CubeFace getOnCubeFace(Long id) {
		return cubeRepo.findById(id);
	}


	public CubeFace updateCubeFace(Long id, CubeFace cubeFace2) {
		CubeFace cubeFace = cubeRepo.findById(id);
		
		if(cubeFace.getActivity() != null)
			cubeFace.setActivity(cubeFace.getActivity());
		
		if(cubeFace.getPoints() != null)
			cubeFace.setPoints(cubeFace.getPoints());
		
		if(cubeFace.getTime() != null)
			cubeFace.setTime(cubeFace.getTime());
		
		cubeRepo.save(cubeFace);
		return cubeFace;
	}
	
	
	
}
