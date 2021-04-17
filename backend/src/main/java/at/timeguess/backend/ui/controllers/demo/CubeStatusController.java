package at.timeguess.backend.ui.controllers.demo;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import at.timeguess.backend.model.Cube;
import at.timeguess.backend.model.CubeStatus;
import at.timeguess.backend.model.CubeStatusInfo;
import at.timeguess.backend.repositories.CubeRepository;
import at.timeguess.backend.services.StatusService;
import at.timeguess.backend.spring.UserStatusInitializationHandler;
import at.timeguess.backend.ui.websockets.WebSocketManager;
import at.timeguess.backend.utils.CDIAutowired;
import at.timeguess.backend.utils.CDIContextRelated;

/**
 * This controller holds and manages all user's cube-information (i.e. their
 * online-status)
 *
 * This class is part of the skeleton project provided for students of the
 * courses "Software Architecture" and "Software Engineering" offered by the
 * University of Innsbruck.
 */
@Controller
@Scope("application")
@CDIContextRelated
public class CubeStatusController {

    @Autowired
    private CubeRepository cubeRepository;
    @Autowired
    private StatusService statusService;
    @CDIAutowired
    private WebSocketManager websocketManager;
    
    private Map<Long, CubeStatusInfo> cubeStatus = new ConcurrentHashMap<>();
    private Set<Cube> readyCubes = new HashSet<>();
    
//    private List<LogEntry> actionLogs = new CopyOnWriteArrayList<>();

	/**
     * Called by the {@link UserStatusInitializationHandler}: When the
     * database-connection is established, all cubes can be retrieved and the
     * collection holding the cube-status can be setup.
     */
    @PostConstruct
    public void setupCubeStatus() {   	
        this.cubeRepository.findAll()
                .forEach(cube -> this.cubeStatus.put(cube.getId(), new CubeStatusInfo(cube)));
    }
    
    public void setReadyCubes() {
    	for(Map.Entry<Long, CubeStatusInfo> s : this.cubeStatus.entrySet()) {
    		if(s.getValue().getStatus().equals(CubeStatus.READY))
    			readyCubes.add(s.getValue().getCube());
    	}
    }
    
    
    public void update() {
    	setCubeStatus(statusService.getCubeStatus());
    	setReadyCubes();
    	this.websocketManager.getCubeChannel().send("connectionCubeUpdate");
    }
    

    public void setCubeStatus(Map<Long, CubeStatusInfo> cubeStatus) {
		this.cubeStatus = cubeStatus;
	}

	/**
     * @return collection of cube-status
     */
    public Collection<CubeStatusInfo> getCubeStatusInfos() {
 
        return Collections.unmodifiableCollection(this.cubeStatus.values());
    }
    
    public Set<Cube> getReadyCubes() {
 		return this.readyCubes;
 	}    
    
    /**........  IF SOME LOG DATA WOULD LIKE BE ADDED.......
     * Simply appends a log-entry to the action-log.
     *
     * @param user
     * @param type
     */
    /*   private void log(User user, LogEntryType type) {
        this.actionLogs.add(new LogEntry(user, type));
    }

    public List<LogEntry> getActionLogs() {
        return Collections.unmodifiableList(this.actionLogs);
    }
*/
}
