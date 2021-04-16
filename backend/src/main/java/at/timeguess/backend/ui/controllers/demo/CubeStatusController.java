package at.timeguess.backend.ui.controllers.demo;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import at.timeguess.backend.events.OnboardingEventPublisher;
import at.timeguess.backend.model.Cube;
import at.timeguess.backend.model.CubeStatus;
import at.timeguess.backend.model.User;
import at.timeguess.backend.model.demo.LogEntry;
import at.timeguess.backend.model.demo.LogEntryType;
import at.timeguess.backend.model.demo.UserStatus;
import at.timeguess.backend.model.demo.UserStatusInfo;
import at.timeguess.backend.repositories.CubeRepository;
import at.timeguess.backend.repositories.UserRepository;
import at.timeguess.backend.services.OnboardingService;
import at.timeguess.backend.spring.UserStatusInitializationHandler;
import at.timeguess.backend.ui.websockets.WebSocketManager;
import at.timeguess.backend.utils.CDIAutowired;
import at.timeguess.backend.utils.CDIContextRelated;

/**
 * This controller holds and manages all user's status-information (i.e. their
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
	private OnboardingService onboardingService;
    @Autowired
    private CubeRepository cubeRepository;
    @CDIAutowired
    private WebSocketManager websocketManager;
    
    
    private Map<String, Cube> cubeStatus = new ConcurrentHashMap<>();
    private Map<String, Cube> cubeStatusTwo = new ConcurrentHashMap<>();
    
    
//    private List<LogEntry> actionLogs = new CopyOnWriteArrayList<>();

    /**
     * Called by the {@link UserStatusInitializationHandler}: When the
     * database-connection is established, all users can be retrieved and the
     * collection holding the user-status can be setup.
     *
     * NOTE: The {@link UserStatusInitializationHandler} calls this setup-method
     * only once at startup. If you add any other users, they will not appear within
     * the user-status-collection. You either need to restart the application
     * (feasible in development-mode, really bad behavior in production) or call
     * this setup.method again to refresh the mentioned collection
     */
    @PostConstruct
    public void setupCubeStatus() {
        this.cubeRepository.findAll()
                .forEach(cube -> this.cubeStatus.put(cube.getMacAddress(), cube));
    }
    
    public void statusChange() {
   /*     setupCubeStatus();
        Cube cubeChanged = onboardingService.getCube();
        this.cubeStatus.put(cubeChanged.getMacAddress(), cubeChanged);
        System.out.println(cubeChanged.getCubeStatus());
     */ 
    	this.cubeStatus=onboardingService.getCubeStatus();
    	
    	this.websocketManager.getCubeChannel().send("connectionCubeUpdate");
    }


    /**
     * Simply appends a log-entry to the action-log.
     *
     * @param user
     * @param type
     */
    /*   private void log(User user, LogEntryType type) {
        this.actionLogs.add(new LogEntry(user, type));
    }
*/
    public Collection<Cube> getCubeStatusInfos() {
        return Collections.unmodifiableCollection(this.cubeStatus.values());
    }
/*
    public List<LogEntry> getActionLogs() {
        return Collections.unmodifiableList(this.actionLogs);
    }
*/
}
