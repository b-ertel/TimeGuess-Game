package at.timeguess.backend.api;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import at.timeguess.backend.model.api.HealthMessage;
import at.timeguess.backend.model.api.HealthResponse;

/**
 * REST Controller for communication with Raspberry Pi.
 */
@RestController
public class HealthController {

    /**
     * Process messages from a Raspberry Pi signaling
     * general availability and containing information on battery
     * level and signal strength of the TimeFlip device.
     * 
     * @param message the message
     * @return the response
     */
    @PostMapping("/api/health")
    private HealthResponse processHealth(@RequestBody HealthMessage message) {
        // just a placeholder for the moment
        // "real" processing of the message and generation of response should happen in a service
        HealthResponse response = new HealthResponse();
        response.setSuccess(true);
        return response;
    }

}
