package at.timeguess.backend.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import at.timeguess.backend.events.OnboardingEventPublisher;
import at.timeguess.backend.model.api.StatusMessage;
import at.timeguess.backend.model.api.StatusResponse;

@Service
public class StatusService {

    private static final Logger LOGGER = LoggerFactory.getLogger(StatusService.class);
    
    @Autowired
    OnboardingEventPublisher onboardingEventPublisher;
    @Autowired
    CubeService cubeService;

    public StatusResponse processStatus(StatusMessage message) {

    	LOGGER.info("new Cube is onboarding.....");
    	
    	cubeService.updateCube(message);
        
        onboardingEventPublisher.publishOnboardingEvent();

        StatusResponse response = new StatusResponse();
        response.setReportingInterval(10);
        return response;
    }
    
    
}
