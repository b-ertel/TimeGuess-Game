package at.timeguess.backend.spring;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import at.timeguess.backend.model.Cube;
import at.timeguess.backend.ui.controllers.demo.CubeStatusController;
import at.timeguess.backend.ui.controllers.demo.UserStatusController;

/**
 * This handler is triggered after the application-context is refreshed, i.e.
 * configurations are setup.
 *
 * This class is part of the skeleton project provided for students of the
 * courses "Software Architecture" and "Software Engineering" offered by the
 * University of Innsbruck.
 *
 */
@Component
public class UserStatusInitializationHandler implements ApplicationListener<ContextRefreshedEvent> {

    @Autowired
    private UserStatusController userStatusController;
    @Autowired
    private CubeStatusController cubeStatusController;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        // init
        this.userStatusController.setupUserStatus();
        this.cubeStatusController.setupCubeStatus();
        
        for(Cube c : this.cubeStatusController.getCubeStatusInfos()) {
        	System.out.println(c.getMacAddress() + ", " + c.getName() + ", " + c.getCubeStatus() + c.getCubeStatus().getMessage());
        }

    }

}
