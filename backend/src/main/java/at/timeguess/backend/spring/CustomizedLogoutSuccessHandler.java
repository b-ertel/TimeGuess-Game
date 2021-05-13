package at.timeguess.backend.spring;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.authentication.logout.SimpleUrlLogoutSuccessHandler;
import org.springframework.stereotype.Component;

import at.timeguess.backend.ui.controllers.UserStatusController;

/**
 * This handler is triggered after a logout is performed.
 *
 * This class is part of the skeleton project provided for students of the
 * courses "Software Architecture" and "Software Engineering" offered by the
 * University of Innsbruck.
 *
 */
@Component
public class CustomizedLogoutSuccessHandler extends SimpleUrlLogoutSuccessHandler implements LogoutSuccessHandler {

    @Autowired
    private UserStatusController userStatusController;


    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
                    throws IOException, ServletException {
        if(authentication != null) { 
        	String username = authentication.getName();
        	// update online-status
        	this.userStatusController.afterLogout(username);
        	// continue as expected
        	super.onLogoutSuccess(request, response, authentication);
        }
    }

}
