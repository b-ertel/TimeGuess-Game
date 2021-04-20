package at.timeguess.backend.api;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import at.timeguess.backend.model.api.OnboardingMessage;
import at.timeguess.backend.model.api.OnboardingResponse;

/**
 * REST Controller for communication with Raspberry Pi.
 */
@RestController
public class OnboardingController {

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

}
