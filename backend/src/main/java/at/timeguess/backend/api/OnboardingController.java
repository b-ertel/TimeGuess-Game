package at.timeguess.backend.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import at.timeguess.backend.model.api.OnboardingMessage;
import at.timeguess.backend.model.api.OnboardingResponse;
import at.timeguess.backend.services.OnboardingService;

/**
 * REST Controller for communication with Raspberry Pi.
 */
@RestController
public class OnboardingController {

	@Autowired
	OnboardingService onboardingService;
	
    /**
     * Process messages from a Raspberry pi signaling
     * successful startup and connection with a TimeFlip device.
     * 
     * @param message the message
     * @return the response
     */
    @PostMapping("/api/onboarding")
    private OnboardingResponse processOnboarding(@RequestBody OnboardingMessage message) {
    	return onboardingService.processOnboarding(message);
    }

}
