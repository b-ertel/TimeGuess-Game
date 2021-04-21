package at.timeguess.backend.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import at.timeguess.backend.services.CubeService;
import at.timeguess.backend.model.api.FacetsMessage;
import at.timeguess.backend.model.api.FacetsResponse;

/**
 * REST Controller for communication with Raspberry Pi.
 */
@RestController
public class FacetsController {

    @Autowired
    private CubeService cubeService;

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

}
