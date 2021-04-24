package at.timeguess.backend.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import at.timeguess.backend.services.CubeService;
import at.timeguess.backend.ui.controllers.StatusController;
import at.timeguess.backend.model.api.FacetsMessage;
import at.timeguess.backend.model.api.FacetsResponse;
import at.timeguess.backend.model.api.StatusMessage;
import at.timeguess.backend.model.api.StatusResponse;

/**
 * REST Controller for communication with Raspberry Pi.
 */
@RestController
public class RaspberryPiController {

    @Autowired
    private CubeService cubeService;
    
    @Autowired
    private StatusController statusController;

    /**
     * Process messages from a TimeFlip device signaling
     * a change of the facets characteristic.
     * 
     * @param message the message
     * @return the response
     */
    @PostMapping("/api/facets")
    private FacetsResponse processFacets(@RequestBody FacetsMessage message) {
        return cubeService.processFacets(message);
    }
    
    /**
     * manages status messages of timeflip device
     * 
     * @param message from the timeflip device from online cube
     * @return status response with an interval
     */
    @PostMapping("/api/status")
    private StatusResponse processStatus(@RequestBody StatusMessage message) {
        return statusController.processStatus(message);
    }

}
