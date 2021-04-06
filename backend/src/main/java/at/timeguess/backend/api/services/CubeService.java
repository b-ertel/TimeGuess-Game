package at.timeguess.backend.api.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import at.timeguess.backend.model.Cube;
import at.timeguess.backend.repositories.CubeRepository;

@Service
public class CubeService {

	private Cube cube;
	
	@Autowired
	private CubeRepository cubeRepo;

	public void saveCube(Cube cube) {
		cubeRepo.save(cube);
	}

	public Cube getCube() {
		return this.cube;
	}

	public void setCube(Cube cube) {
		this.cube = cube;
	}

	public List<Cube> getAllCubes() {
		return cubeRepo.findAll();
	}

	public boolean isMacAddressKnown(Cube cube) {
		if(cubeRepo.findByMacAddress(cube.getMacAddress())==null){
			return false;
		}
		else {
			return true;
		}
	}




	
	
	
}
