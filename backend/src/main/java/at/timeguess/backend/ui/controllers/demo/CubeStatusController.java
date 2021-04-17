package at.timeguess.backend.ui.controllers.demo;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import at.timeguess.backend.model.Cube;
import at.timeguess.backend.model.CubeStatus;
import at.timeguess.backend.repositories.CubeRepository;
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
    @CDIAutowired
    private WebSocketManager websocketManager;
    
    private Map<String, Cube> cubeStatus = new ConcurrentHashMap<>();
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
                .forEach(cube -> this.cubeStatus.put(cube.getMacAddress(), cube));
        setReadyCubes();
    }
    
    public void setReadyCubes() {
    	for(Map.Entry<String, Cube> m : cubeStatus.entrySet()) {
    		if(m.getValue().getCubeStatus().equals(CubeStatus.READY))
    			readyCubes.add(m.getValue());
    	}
    	for(Cube c: this.readyCubes) {
    		System.out.println(c);
    	}
    	System.out.println("end");
    }
    
    /**
     * method which is invoked by the {@link OnboardingEventListener} when a 
     * new or already known cube is online and connected throw the {@link OnboardingController}
     */
    public void statusChange() {
        setupCubeStatus();
    	this.websocketManager.getCubeChannel().send("connectionCubeUpdate");
    }

    /**
     * @return collection of cube-status
     */
    public Collection<Cube> getCubeStatusInfos() {
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
