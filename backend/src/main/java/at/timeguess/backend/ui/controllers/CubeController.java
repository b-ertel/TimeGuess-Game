package at.timeguess.backend.ui.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import at.timeguess.backend.services.CubeService;
import at.timeguess.backend.ui.beans.MessageBean;
import at.timeguess.backend.model.Cube;

/**
 *  Controller for displaying {@link Cube} entities.
 *
 */
@Component
@Scope("session")
public class CubeController {  

    @Autowired
    private CubeService cubeService;
    @Autowired
    private CubeStatusController statusController;
    @Autowired
    private MessageBean message;

    private Cube cube;

    /**
     * saves cube into the database and updates properties in its {@link CubeStatus}
     * and updates current display of {@link CubeStatus} through websockets
     * 
     */
    public void saveCube() {
        try {
            this.cube=cubeService.saveCube(this.cube);
            statusController.updateCubeInStatus(this.cube);
            statusController.updateSockets();
            message.alertInformation("CubeManagment", "Cube saved");
        }
        catch (IllegalArgumentException e) {
            message.alertErrorFailValidation("CubeManagement", e.getMessage());
        }
    }

    /**
     * @return all cubes in the databes
     */
    public List<Cube> getAllCubes() {
        return cubeService.allCubes();
    }

    /**
     * to check if there exists a cube with a given mac address
     * 
     * @param cube to check if it is in database
     * @return true if it is in database, false otherwise
     */
    public boolean isMacAddressKnown(Cube cube){
        return cubeService.isMacAddressKnown(cube.getMacAddress());
    }
    
    /**
     * deletes cube and removes its status
     */
    public void deleteCube() {
        try {
            cubeService.deleteCube(this.cube);
            statusController.updateSockets();
            statusController.deleteStatus(this.cube.getMacAddress());
            message.alertInformation("CubeManagment", "Cube " + this.cube.getId() + " deleted");
        }
        catch (IllegalArgumentException e) {
            message.alertErrorFailValidation("CubeManagement", e.getMessage());
        }
    }
    
    /**
     * deletes configuration of cube
     */
    public void deleteConfigurations() {
        cubeService.deleteConfigurations(cube);
        message.alertInformation("CubeManagment", "Configurations for cube " + this.cube.getId() + " deleted");
    }
    
    public Cube getCube() {
        return this.cube;
    }

    public void setCube(Cube cube) {
        this.cube = cube;
    }
}


