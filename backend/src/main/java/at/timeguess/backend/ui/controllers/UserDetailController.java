package at.timeguess.backend.ui.controllers;

import java.io.Serializable;

import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import at.timeguess.backend.model.User;
import at.timeguess.backend.services.UserService;
import at.timeguess.backend.ui.beans.MessageBean;

/**
 * Controller for the user detail view.
 */
@Component
@Scope("view")
public class UserDetailController implements Serializable {

    private static final long serialVersionUID = 1L;

    @Autowired
    private UserService userService;

    @Autowired
    private MessageBean messageBean;

    /**
     * Attribute to cache the currently displayed user
     */
    private User user;

    /**
     * Sets the currently displayed user and reloads it form db.
     * This user is targeted by any further calls of {@link #doReloadUser()}, {@link #doSaveUser()} and {@link #doDeleteUser()}.
     * @param user
     */
    public void setUser(User user) {
        this.user = user;
        doReloadUser();
    }

    /**
     * Returns the currently displayed user.
     * @return
     */
    public User getUser() {
        return user;
    }

    /**
     * Action to force a reload of the currently displayed user.
     */
    public void doReloadUser() {
        user = userService.loadUser(user.getId());
    }

    /**
     * Action to save the currently displayed user.
     */
    public void doSaveUser() {
        if (doValidateUser()) user = this.userService.saveUser(user);
        else messageBean.alertErrorFailValidation("Saving user failed", "Input fields are invalid");
    }

    /**
     * Action to delete the currently displayed user.
     */
    public void doDeleteUser() {
        this.userService.deleteUser(user);
        user = null;
    }

    /**
     * Checks if all fields contain valid values.
     * @return true if all fields contain valid values, false otherwise
     */
    public boolean doValidateUser() {
        if (Strings.isBlank(user.getUsername())) return false;
        if (Strings.isBlank(user.getPassword())) return false;
        if (user.getRoles() == null || user.getRoles().size() == 0) return false;
        return true;
    }
}
