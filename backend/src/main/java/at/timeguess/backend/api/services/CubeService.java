package at.timeguess.backend.api.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import at.timeguess.backend.model.Cube;
import at.timeguess.backend.model.CubeFace;
import at.timeguess.backend.repositories.CubeFaceRepository;

@Service
public class CubeService {

	private Cube cube;
	
	@Autowired
	private CubeRepository cubeRepo;

	public Cube addCube(Cube cube) {
		this.cube = new Cube();
		this.cube.setId(cube.getId());
		this.cube.setDeviceNo(cube.getDeviceNo());

		
		return this.cube;
	}
	

	public void saveCube(Cube cube2) {
		// TODO Auto-generated method stub
		
	}


	public Cube getCube() {
		return cube;
	}


	public void setCube(Cube cube) {
		this.cube = cube;
	}


	public List<Cube> getAllCubes() {
		// TODO Auto-generated method stub
		return null;
	}




	
	
	
}
