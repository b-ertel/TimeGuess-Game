package at.timeguess.backend.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import at.timeguess.backend.events.FacetsEventPublisher;
import at.timeguess.backend.events.OnboardingEventPublisher;
import at.timeguess.backend.model.Cube;
import at.timeguess.backend.model.CubeStatus;
import at.timeguess.backend.model.api.FacetsMessage;
import at.timeguess.backend.model.api.FacetsResponse;
import at.timeguess.backend.model.api.OnboardingMessage;
import at.timeguess.backend.model.api.OnboardingResponse;
import at.timeguess.backend.repositories.CubeRepository;

@Service
public class OnboardingService {

    private Cube cube = new Cube();
    private Map<String, Cube> cubeStatusTwo = new ConcurrentHashMap<>();

    private static final Logger LOGGER = LoggerFactory.getLogger(OnboardingService.class);
    
    @Autowired
    OnboardingEventPublisher onboardingEventPublisher;
    @Autowired
    CubeService cubeService;
    @Autowired
    CubeRepository cubeRepo;

    public OnboardingResponse processOnboarding(OnboardingMessage message) {

    	setupCubeStatus();
        this.cube = cubeService.updateCube(message);
    	addCubeStatus(this.cube);
        
        LOGGER.info("now event will be published");
        
        onboardingEventPublisher.publishOnboardingEvent();

        LOGGER.info("now event is published");
        
        OnboardingResponse response = new OnboardingResponse();
        response.setSuccess(true);
        return response;
    }
    


    public Cube getCube() {
        return this.cube;
    }

    public void setCube(Cube cube) {
        this.cube = cube;
    }
    
    public void setupCubeStatus() {
        this.cubeRepo.findAll()
                .forEach(cube -> this.cubeStatusTwo.put(cube.getMacAddress(), cube));
    }
    
    public Map<String, Cube> getCubeStatus() {
    	return this.cubeStatusTwo;
    }
    
    public void addCubeStatus(Cube cube) {
    	this.cubeStatusTwo.put(cube.getMacAddress(), cube);
    }
    
}
