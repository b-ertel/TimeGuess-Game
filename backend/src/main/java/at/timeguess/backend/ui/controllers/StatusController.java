package at.timeguess.backend.ui.controllers;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PostConstruct;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import at.timeguess.backend.services.CubeService;

import at.timeguess.backend.ui.websockets.WebSocketManager;
import at.timeguess.backend.utils.CDIAutowired;
import at.timeguess.backend.utils.CDIContextRelated;
import at.timeguess.backend.model.Cube;
import at.timeguess.backend.model.CubeStatus;
import at.timeguess.backend.model.CubeStatusInfo;
import at.timeguess.backend.model.HealthStatus;
import at.timeguess.backend.model.api.StatusMessage;
import at.timeguess.backend.model.api.StatusResponse;

/**
 * Controller for management of cube status.
 */
@Controller
@Scope("application")
@CDIContextRelated
public class StatusController {

    // the value of the calibration version characteristic after
    // a reset of the TimeFlip device
    private static final int CALIBRATION_VERSION_AFTER_RESET = 0;
    // the value of the calibration version characteristic to be written
    // to a TimeFlip device after it has successfully established
    // a connection with the backend
    private static final int CALIBRATION_VERSION_AFTER_CONNECTION = 1;

    private static final Logger LOGGER = LoggerFactory.getLogger(StatusController.class);
	
    @Autowired
    private CubeService cubeService;
    
    @CDIAutowired
    private WebSocketManager websocketManager;
    
    private Map<String, CubeStatusInfo> cubeStatus = new ConcurrentHashMap<>();

	private Map<String, HealthStatus> healthStatus = new ConcurrentHashMap<>();
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
    public StatusResponse processStatus(StatusMessage message) {

    	if(this.healthStatus.containsKey(message.getIdentifier())){
    		LOGGER.info("status message received.....");
    		this.healthStatus.get(message.getIdentifier()).setBatteryLevel(message.getBatteryLevel());
    		this.healthStatus.get(message.getIdentifier()).setRssi(message.getRssi());
    		this.healthStatus.get(message.getIdentifier()).setTimestamp(LocalDateTime.now());
    	}
    	else {
    		LOGGER.info("cube is onboarding.....");
    		this.healthStatus.put(message.getIdentifier(), new HealthStatus(LocalDateTime.now(), message.getBatteryLevel(), message.getRssi(), message.getIdentifier()));
    		setInterval(10);
    		updateCube(message); 
    	}
   	
        StatusResponse response = new StatusResponse();
        response.setReportingInterval(this.interval);
        response.setCalibrationVersion(CALIBRATION_VERSION_AFTER_CONNECTION);
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

			// delete any existing configurations if a reset of the TimeFlip device is detected
			if (message.getCalibrationVersion() == CALIBRATION_VERSION_AFTER_RESET) {
			    LOGGER.info("calibration version is 0 --> cube lost configuration");
				cubeService.deleteConfigurations(updatedCube);
			}
			
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
        updateSockets();
    }
        
	/**
     * @return collection of cube-status
     */
    public Collection<CubeStatusInfo> getCubeStatusInfos() {
        return Collections.unmodifiableCollection(this.cubeStatus.values());
    }  
    
    /**
     * returns cubes with status {@link CubeStatus.READY}
     */
    public Set<Cube> getReadyCubes() {
    	
    	Set<Cube> readyCubes = new HashSet<>();
    	
    	for(Map.Entry<String, CubeStatusInfo> s : this.cubeStatus.entrySet()) {
    		if(s.getValue().getStatus().equals(CubeStatus.READY))
    			readyCubes.add(s.getValue().getCube());
    	}
    	return readyCubes;
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
	 * changes status of cube to {@link CubeStatus.IN_CONFIG}
	 * @param macAddress of the cubes which should be set to {@link CubeStatus.IN_CONFIG}
	 */
	public void setInConfig(String macAddress) {
		statusChange(macAddress, CubeStatus.IN_CONFIG);
	}
	
	/**
	 * changes status of cube to {@link CubeStatus.IN_GAME}
	 * @param macAddress of the cubes which should be set to {@link CubeStatus.IN_GAME}
	 */
	public void setInGame(String macAddress) {
		statusChange(macAddress, CubeStatus.IN_GAME);
	}
	
	/**
	 * changes status of cube to {@link CubeStatus.READY}
	 * @param macAddress of the cubes which should be set to {@link CubeStatus.READY}
	 */
	public void setReady(String macAddress) {
		statusChange(macAddress, CubeStatus.READY);
	}
	
	/**
	 * changes status of cube to {@link CubeStatus.LIVE}
	 * @param macAddress of the cubes which should be set to {@link CubeStatus.LIVE}
	 */
	public void setLive(String macAddress) {
		statusChange(macAddress, CubeStatus.LIVE);
	}
	
	/**
	 * gets status of a given cube
	 * 
	 * @param macAddress of cube to get its status
	 * @return CubeStatus
	 */
	public CubeStatus getStatus(String macAddress) {
		return this.cubeStatus.get(macAddress).getStatus();
	}
	
	/**
	 * checks if a Cube is configured i.e. if there is any entry for it in the Configuration Table
	 * 
	 * @return true if it has a Configuration, false otherwise
	 */
	public boolean isConfigured(Cube cube) {
		return cubeService.isConfigured(cube);
	}
    
    /**
     * checks if a Cube has status {@link CubeStatus.READY}
     * 
     * @param cube cube to get its ready status
     * @return true if has status {@link CubeStatus.READY}, false otherwise
     */
    public boolean isReady(Cube cube) {
        return this.cubeStatus.get(cube.getMacAddress()).getStatus() == CubeStatus.READY;
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
		updateSockets();
	}
	
	/** changes Status if configuration is deleted via UI
	 * @param macAddress of cube which status should be changed
	 */
	public void changeStatus(String macAddress) {
		if(this.cubeStatus.get(macAddress).getStatus().equals(CubeStatus.READY)){
			statusChange(macAddress, CubeStatus.LIVE);
		}
		else {
			statusChange(macAddress, CubeStatus.OFFLINE);
		}
	}
	
	/**
	 * check if a cube can be deleted - i.e. if its status is OFFLINE
	 * 
	 * @param mac to check status
	 * @return true if status is OFFLINE false otherwise
	 */
	public boolean checkDeletion(String mac) {
		if(cubeStatus.get(mac) != null) {
			if(cubeStatus.get(mac).getStatus().equals(CubeStatus.OFFLINE)) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * check if a cube can be configured - i.e. if its status is LIVE or READY
	 * 
	 * @param mac to check status
	 * @return true if status is LIVE or READY false otherwise
	 */
	public boolean checkConfiguration(String mac) {
		if(cubeStatus.get(mac) != null) {
			if(cubeStatus.get(mac).getStatus().equals(CubeStatus.LIVE) ||
					cubeStatus.get(mac).getStatus().equals(CubeStatus.READY)) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * sends a "connectionCubeUpdate" to all socket listener
	 */
	public void updateSockets() {
		this.websocketManager.getCubeChannel().send("connectionCubeUpdate");
	}

	/** updates cube in CubeStatusInfo
	 * @param cube to update
	 */
	public void updateCube(Cube cube) {
		this.cubeStatus.get(cube.getMacAddress()).setCube(cube);
	}
	
}
