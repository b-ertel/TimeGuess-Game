package at.timeguess.backend.spring;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.InteractiveAuthenticationSuccessEvent;
import org.springframework.stereotype.Component;


import at.timeguess.backend.ui.controllers.UserStatusController;

/**
 * This handler is triggered after a login is performed.
 *
 * This class is part of the skeleton project provided for students of the
 * courses "Software Architecture" and "Software Engineering" offered by the
 * University of Innsbruck.
 *
 */
@Component
public class LoginSuccessHandler implements ApplicationListener<InteractiveAuthenticationSuccessEvent> {

    @Autowired
    private UserStatusController userStatusController;

    @Override
    public void onApplicationEvent(InteractiveAuthenticationSuccessEvent event) {
        String username = event.getAuthentication().getName();
        // update online-status
        this.userStatusController.afterLogin(username);
    }

}
