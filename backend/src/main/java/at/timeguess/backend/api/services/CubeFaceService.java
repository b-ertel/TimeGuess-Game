package at.timeguess.backend.api.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import at.timeguess.backend.model.Cube;
import at.timeguess.backend.model.CubeFace;
import at.timeguess.backend.repositories.CubeFaceRepository;

@Service
public class CubeFaceService {

	private CubeFace cubeFace;
	private Cube cube;
	
	@Autowired
	private CubeFaceRepository cubeRepo;

	public CubeFace addCubeFace(CubeFace cubeFace) {
		this.cubeFace = new CubeFace();
	//	this.cubeFace.setActivity(cubeFace.getActivity());
	//	this.cubeFace.setPoints(cubeFace.getPoints());
	//	this.cubeFace.setTime(cubeFace.getTime());
		this.cubeFace.setId(cubeFace.getId());
	//	this.cubeFace.setCube(this.cube);
		cubeRepo.save(cubeFace);
		return this.cubeFace;
	}
	
	public void saveCubeFace(CubeFace cubeFace) {
		cubeRepo.save(cubeFace);
	}


/*	public CubeFace updateCubeFace(CubeFace cubeFace) {
		
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

*/
	public Cube getCube() {
		return cube;
	}


	public void setCube(Cube cube) {
		this.cube = cube;
	}

	public List<CubeFace> getAllCubeFaces() {
		return cubeRepo.findAll();
		
	}
	
	
	
}
