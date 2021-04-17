package at.timeguess.backend.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import at.timeguess.backend.events.OnboardingEventPublisher;
import at.timeguess.backend.model.api.OnboardingMessage;
import at.timeguess.backend.model.api.OnboardingResponse;

@Service
public class OnboardingService {

    private static final Logger LOGGER = LoggerFactory.getLogger(OnboardingService.class);
    
    @Autowired
    OnboardingEventPublisher onboardingEventPublisher;
    @Autowired
    CubeService cubeService;

    public OnboardingResponse processOnboarding(OnboardingMessage message) {

    	LOGGER.info("new Cube is onboarding.....");
    	
    	cubeService.updateCube(message);
        
        onboardingEventPublisher.publishOnboardingEvent();

        OnboardingResponse response = new OnboardingResponse();
        response.setSuccess(true);
        return response;
    }
    
    
}
