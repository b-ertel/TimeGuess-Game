package at.timeguess.backend.api;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import at.timeguess.backend.services.CubeService;

import at.timeguess.backend.model.api.OnboardingMessage;
import at.timeguess.backend.model.api.OnboardingResponse;
import at.timeguess.backend.model.api.HealthMessage;
import at.timeguess.backend.model.api.HealthResponse;
import at.timeguess.backend.model.api.FacetsMessage;
import at.timeguess.backend.model.api.FacetsResponse;

/**
 * REST Controller for communication with Raspberry Pi.
 */
@RestController
public class Controller {
	
	@Autowired
	private CubeService cubeService;

    /**
     * Process messages from a Raspberry pi signaling
     * successful startup and connection with a TimeFlip device.
     * 
     * @param message the message
     * @return the response
     */
    @PostMapping("/api/onboarding")
    private OnboardingResponse processOnboarding(@RequestBody OnboardingMessage message) {
        // just a placeholder for the moment
        // "real" processing of the message and generation of response should happen in a service
        OnboardingResponse response = new OnboardingResponse();
        response.setSuccess(true);
        return response;
    }

    /**
     * Process messages from a TimeFlip device signaling
     * a change of the facets characteristic.
     * 
     * @param message the message
     * @return the response
     */
    @PostMapping("/api/facets")
    private FacetsResponse processFacets(@RequestBody FacetsMessage message) {
        // do something ...
        FacetsResponse response = new FacetsResponse();
        response.setSuccess(true);
        response.setConfiguration(0);
        return response;
    }

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

