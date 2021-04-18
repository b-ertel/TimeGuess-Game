package at.timeguess.backend.ui.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;

import at.timeguess.backend.services.CubeService;
import at.timeguess.backend.model.Cube;

/**
 *  Controller for displaying {@link Cube} entities.
 *
 */
@Component
@Scope("view")
public class CubeController {

    @Autowired
    private CubeService cubeService;

    private Cube cube;

    /**
     * @param cube to register -> i.e. which has to be saved in the database
     * @return registered Cube
     */
    @PreAuthorize("hasAuthority('ADMIN')")
    public Cube registerCube(Cube cube) {

        this.cube = new Cube();
        this.cube.setId(cube.getId());
        this.cube.setMacAddress(cube.getMacAddress());
        this.cube.setName(cube.getName());
        saveCube();

        return this.cube;

    }

    public Cube getCube() {
        return this.cube;
    }

    public void setCube(Cube cube) {
        this.cube = cube;
    }

    public void saveCube() {
        this.cube=cubeService.saveCube(this.cube);
    }

    public List<Cube> getAllCubes() {
        return cubeService.allCubes();
    }

    public boolean isMacAddressKnown(Cube cube){
        return cubeService.isMacAddressKnown(cube.getMacAddress());
    }
    
    public void print() {
    	System.out.println("this cube: " + this.cube);
    }
    
    public void deleteCube() {
    	cubeService.deleteCube(this.cube);
    }

}

