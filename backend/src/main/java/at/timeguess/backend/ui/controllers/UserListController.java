package at.timeguess.backend.ui.controllers;

import java.io.Serializable;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;

import at.timeguess.backend.model.User;
import at.timeguess.backend.services.UserService;

/**
 * Controller for the user list view.
 *
 */
@Component
@Scope(WebApplicationContext.SCOPE_SESSION)
public class UserListController implements Serializable {

    private static final long serialVersionUID = 1L;
	
	@Autowired
    private UserService userService;

    /**
     * Returns a list of all users.
     *
     * @return
     */
    public List<User> getUsers() {
        return userService.getAllUsers();
    }

    /**
     * Returns a list of all users with role 'PLAYER'.
     *
     * @return
     */
    public List<User> getAllPlayers() {
        return userService.getAllPlayers();
    }

}
