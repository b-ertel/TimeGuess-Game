package at.timeguess.backend.api;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PostConstruct;

import java.util.Collections;
import java.util.HashSet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import at.timeguess.backend.services.StatusService;
import at.timeguess.backend.ui.websockets.WebSocketManager;
import at.timeguess.backend.utils.CDIAutowired;
import at.timeguess.backend.utils.CDIContextRelated;
import at.timeguess.backend.model.Cube;
import at.timeguess.backend.model.CubeStatus;
import at.timeguess.backend.model.CubeStatusInfo;
import at.timeguess.backend.model.api.StatusMessage;
import at.timeguess.backend.model.api.StatusResponse;

/**
 * REST Controller for communication with Raspberry Pi.
 */
@RestController
@Scope("application")
@CDIContextRelated
public class StatusController {

    @Autowired
    private StatusService statusService;
    @CDIAutowired
    private WebSocketManager websocketManager;
    
    private Map<Long, CubeStatusInfo> cubeStatus = new ConcurrentHashMap<>();
    private Set<Cube> readyCubes = new HashSet<>();
    private Cube cube;
    
    @PostConstruct
    public void setupCubeStatus() {   	
        setCubeStatus(statusService.getCubeStatus());
    	setReadyCubes();
    }
    
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
    
	/**
     * @return collection of cube-status
     */
    public Collection<CubeStatusInfo> getCubeStatusInfos() {
        return Collections.unmodifiableCollection(this.cubeStatus.values());
    }
    
    /**
     * update method which is called by {@link OnboardingEventListener}
     */
    public void update() {
    	setCubeStatus(statusService.getCubeStatus());
    	setReadyCubes();
    	this.websocketManager.getCubeChannel().send("connectionCubeUpdate");
    }
    
    
    public void setReadyCubes() {
    	for(Map.Entry<Long, CubeStatusInfo> s : this.cubeStatus.entrySet()) {
    		if(s.getValue().getStatus().equals(CubeStatus.READY))
    			readyCubes.add(s.getValue().getCube());
    	}
    }
    
    public void setCubeStatus(Map<Long, CubeStatusInfo> cubeStatus) {
		this.cubeStatus = cubeStatus;
	}
    
    public Set<Cube> getReadyCubes() {
 		return this.readyCubes;
 	}

	public Cube getCube() {
		return cube;
	}

	public void setCube(Cube cube) {
		this.cube = cube;
	}  
	
	public void print() {
		System.out.println(this.cube);
	}

}
