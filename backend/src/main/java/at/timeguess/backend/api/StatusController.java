package at.timeguess.backend.api;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PostConstruct;

import java.util.Collections;
import java.util.HashSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import at.timeguess.backend.services.CubeService;

import at.timeguess.backend.ui.websockets.WebSocketManager;
import at.timeguess.backend.utils.CDIAutowired;
import at.timeguess.backend.utils.CDIContextRelated;
import at.timeguess.backend.model.Cube;
import at.timeguess.backend.model.CubeStatus;
import at.timeguess.backend.model.CubeStatusInfo;
import at.timeguess.backend.model.api.HealthStatus;
import at.timeguess.backend.model.api.StatusMessage;
import at.timeguess.backend.model.api.StatusResponse;

/**
 * REST Controller for communication with Raspberry Pi and management of cube status.
 */
@Controller
@Scope("application")
@CDIContextRelated
public class StatusController {

    private static final Logger LOGGER = LoggerFactory.getLogger(StatusController.class);
	
    @Autowired
    private CubeService cubeService;
    
    @CDIAutowired
    private WebSocketManager websocketManager;
    
    private Map<String, CubeStatusInfo> cubeStatus = new ConcurrentHashMap<>();
    private Map<String, HealthStatus> healthStatus = new ConcurrentHashMap<>();
    private Set<Cube> readyCubes = new HashSet<>();
    private int interval;   // in sec
    
    /**
     * creates a status for each Cube in the database
     */
    @PostConstruct
    public void setupCubeStatus() {   	
        this.cubeService.allCubes()
                .forEach(cube -> this.cubeStatus.put(cube.getMacAddress(), new CubeStatusInfo(cube)));
    }
    
    /**
	 * manages status messages of timeflip device
	 * 
	 * @param message from the timeflip device from online cube
	 * @return status response with an interval
	 */
    @PostMapping("/api/status")
    @ResponseBody
    private StatusResponse processStatus(@RequestBody StatusMessage message) {

    	if(this.healthStatus.containsKey(message.getIdentifier())){
    		LOGGER.info("status message received.....");
    		this.healthStatus.get(message.getIdentifier()).setBatteryLevel(message.getBatteryLevel());
    		this.healthStatus.get(message.getIdentifier()).setRssi(message.getRssi());
    		this.healthStatus.get(message.getIdentifier()).setTimestamp(LocalDateTime.now());
    	}
    	else {
    		LOGGER.info("cube is onboarding.....");
    		updateCube(message);
    		this.healthStatus.put(message.getIdentifier(), new HealthStatus(LocalDateTime.now(), message.getBatteryLevel(), message.getRssi(), message.getIdentifier()));
    		setInterval(10);
    	}
    	
        StatusResponse response = new StatusResponse();
        response.setReportingInterval(this.interval);
        return response;
    }
    
    /**
     * checks if Cube is already in database, create a new entry if not 
     * and sets actual {@link CubeStatus} 
     * 
     * @param message of the cube device
     * @return updated Cube
     */
    public Cube updateCube(StatusMessage message) {
    	
		Cube updatedCube = new Cube();
		
		if(cubeService.isMacAddressKnown(message.getIdentifier())) {						// Cube is already in database
			updatedCube = cubeService.getByMacAddress(message.getIdentifier());
			LOGGER.info("cube is known...");
			
			if(cubeService.isConfigured(updatedCube)){										// Cube is configured and ready
				statusChange(updatedCube.getMacAddress(), CubeStatus.READY);
			}
			else { 
				statusChange(updatedCube.getMacAddress(), CubeStatus.LIVE);					// Cube lost his configuration or has not been configured yet
			}
		}
		else {
			LOGGER.info("cube is not known...new cube is created");
			updatedCube = cubeService.createCube(message);									// Cube is new and has to be created

			this.cubeStatus.put(updatedCube.getMacAddress(), new CubeStatusInfo(updatedCube));
			statusChange(updatedCube.getMacAddress(), CubeStatus.LIVE);
		}
	
		LOGGER.info("cube {} was updated and set status to {}", updatedCube.getId(), this.cubeStatus.get(updatedCube.getMacAddress()).getStatus());
    	
    	return updatedCube;
    }
       

	/**
     * is called in case cube changes its status, updates status in GUI
     */
    public void statusChange(String mac, CubeStatus cubeStatus) {
        this.cubeStatus.get(mac).setStatus(cubeStatus);
        setReadyCubes();

        this.websocketManager.getCubeChannel().send("connectionCubeUpdate");
    }
        
	/**
     * @return collection of cube-status
     */
    public Collection<CubeStatusInfo> getCubeStatusInfos() {
        return Collections.unmodifiableCollection(this.cubeStatus.values());
    }  
    
    /**
     * puts all cubes with status {@link CubeStatus.READY} into readyCubes 
     */
    public void setReadyCubes() {
    	for(Map.Entry<String, CubeStatusInfo> s : this.cubeStatus.entrySet()) {
    		if(s.getValue().getStatus().equals(CubeStatus.READY))
    			readyCubes.add(s.getValue().getCube());
    	}
    }
    
    /**
     * @return ready cubes i.e. cubes with {@link CubeStatus.READY}
     */
    public Set<Cube> getReadyCubes() {
 		return this.readyCubes;
 	}

	/**
	 * @return health status of all online cubes i.e. with status 
	 * {@link CubeStatus.READY, CubeStatus.LIVE or CubeStatus.IN_CONFIGURATION}
	 */
	public Map<String, HealthStatus> getHealthStatus() {
		return healthStatus;
	}

	/**
	 * changes status of cube to {@link CubeStatus.OFFLINE}
	 * @param macAddress of the cubes which should be set to {@link CubeStatus.OFFLINE}
	 */
	public void setOffline(String macAddress) {
		this.healthStatus.remove(macAddress);
		statusChange(macAddress, CubeStatus.OFFLINE);
	}

	/**
	 * @return interval of reporting period of the cube
	 */
	public int getInterval() {
		return interval;
	}

	/** sets reporting interval for the cube
	 * @param intervall
	 */
	public void setInterval(int intervall) {
		this.interval = intervall;
	}

	/** remove status of a deleted user, called by {@link CubeController} if a cube is deleted via UI
	 * @param macAddress of the deleted cube
	 */
	public void deleteStatus(String macAddress) {
		this.cubeStatus.remove(macAddress);
		this.websocketManager.getCubeChannel().send("connectionCubeUpdate");
	}  
	
	
}
