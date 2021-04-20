package at.timeguess.backend.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import at.timeguess.backend.services.StatusService;
import at.timeguess.backend.model.api.StatusMessage;
import at.timeguess.backend.model.api.StatusResponse;

/**
 * REST Controller for communication with Raspberry Pi.
 */
@RestController
public class StatusController {

    @Autowired
    private StatusService statusService;

    /**
     * Process the regular status messages sent by a Raspberry Pi.
     * 
     * @param message the message
     * @return the response
     */
    @PostMapping("/api/status")
    private StatusResponse processStatus(@RequestBody StatusMessage message) {
        return statusService.processStatus(message);
    }

}
