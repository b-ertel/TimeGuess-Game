package at.timeguess.backend.services;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import at.timeguess.backend.events.OnboardingEventPublisher;
import at.timeguess.backend.model.Cube;
import at.timeguess.backend.model.CubeStatus;
import at.timeguess.backend.model.CubeStatusInfo;
import at.timeguess.backend.model.api.StatusMessage;
import at.timeguess.backend.model.api.StatusResponse;

@Service
public class StatusService {

    private static final Logger LOGGER = LoggerFactory.getLogger(StatusService.class);
    
    @Autowired
    OnboardingEventPublisher onboardingEventPublisher;
    @Autowired
    CubeService cubeService;
    
    private Map<Long, CubeStatusInfo> cubeStatus = new ConcurrentHashMap<>();
    
    /**
	 * method to check whether Cube is known and create an entry if it is not
	 * 
	 * @param message from the timeflip device from online cube
	 * @return status response with an interval
	 */
    public StatusResponse processStatus(StatusMessage message) {

    	LOGGER.info("new Cube is onboarding.....");
    	
		Cube updatedCube = new Cube();
		
		if(cubeService.isMacAddressKnown(message.getIdentifier())) {						// Cube is already in database
			updatedCube = cubeService.getByMacAddress(message.getIdentifier());
			LOGGER.info("cube is known...");
			
			if(cubeService.isConfigured(updatedCube)){										// Cube is configured and ready
				statusChange(updatedCube, CubeStatus.READY);

			}
			else { 
				statusChange(updatedCube, CubeStatus.LIVE);									// Cube lost his configuration or has not been configured yet
			}
		}
		else {
			LOGGER.info("cube is not known...new cube is created");
			updatedCube = cubeService.createCube(message);									// Cube is new and has to be created

			this.cubeStatus.put(updatedCube.getId(), new CubeStatusInfo(updatedCube));
			statusChange(updatedCube, CubeStatus.LIVE);
		}
	
		LOGGER.info("cube {} was updated and set status to {}", updatedCube.getId(), this.cubeStatus.get(updatedCube.getId()).getStatus());
        
        onboardingEventPublisher.publishOnboardingEvent();

        StatusResponse response = new StatusResponse();
        response.setReportingInterval(10);
        return response;
    }
    
    @PostConstruct
    public void setupCubeStatus() {   	
        this.cubeService.allCubes()
                .forEach(cube -> this.cubeStatus.put(cube.getId(), new CubeStatusInfo(cube)));
    }
    
    /**
     * method which is invoked by the {@link OnboardingEventListener} when a 
     * new or already known cube is online and connected throw the {@link OnboardingController}
     */
    public void statusChange(Cube cube, CubeStatus cubeStatus) {
        this.cubeStatus.get(cube.getId()).setStatus(cubeStatus);
    }

	public Map<Long, CubeStatusInfo> getCubeStatus() {
		return cubeStatus;
	}


    
    
    
}
