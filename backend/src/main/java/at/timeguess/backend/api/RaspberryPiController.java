package at.timeguess.backend.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import at.timeguess.backend.services.CubeService;
import at.timeguess.backend.ui.controllers.CubeStatusController;
import at.timeguess.backend.model.api.FacetsMessage;
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
    private CubeStatusController statusController;

    /**
     * Process a {@link FacetsMessage}.
     * 
     * @param message the message
     */
    @PostMapping("/api/facets")
    private void processFacetsMessage(@RequestBody FacetsMessage message) {
        cubeService.processFacetsMessage(message);
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
