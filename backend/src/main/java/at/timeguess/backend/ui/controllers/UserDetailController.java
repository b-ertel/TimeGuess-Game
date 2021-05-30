package at.timeguess.backend.ui.controllers;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;

import at.timeguess.backend.model.User;
import at.timeguess.backend.model.UserRole;
import at.timeguess.backend.services.UserService;
import at.timeguess.backend.ui.beans.MessageBean;

/**
 * Controller for the user detail view.
 */
@Component
@Scope(WebApplicationContext.SCOPE_SESSION)
public class UserDetailController implements Serializable {

    private static final long serialVersionUID = 1L;

    @Autowired
    private UserService userService;

    @Autowired
    private MessageBean messageBean;
    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Attribute to cache the currently displayed user
     */
    private User user;
    private String orgPassword;

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
     * Returns true if the currently displayed users password is encrypted, false otherwise.
     * @return
     */
    public boolean hasEncryptedPassword() {
        return user != null && user.getPassword().matches("\\{.+\\}.+");
    }

    /**
     * Returns the roles of the currently displayed user as a list (because Primefaces needs that).
     * @return
     */
    public List<UserRole> getUserRoles() {
        return user == null ? null : new ArrayList<>(user.getRoles());
    }

    /**
     * Sets the roles of the currently displayed user to the given values (because Primefaces can only deal with a
     * list).
     * @return
     */
    public void setUserRoles(List<UserRole> userRoles) {
        if (user != null) user.setRoles(new HashSet<>(userRoles));
    }

    /**
     * Returns a set containing all available user roles.
     * @return
     */
    public Set<UserRole> getAllUserRoles() {
        return UserRole.getUserRoles();
    }

    /**
     * Action to force a reload of the currently displayed user.
     */
    public void doReloadUser() {
        user = userService.loadUser(user);
        orgPassword = user.getPassword();
    }

    /**
     * Action to save the currently displayed user.
     */
    public void doSaveUser() {
        if (this.doValidateUser()) {
            this.checkPasswordChange();

            User ret = null;
            try {
                ret = this.userService.saveUser(user);
                if (ret != null) {
                    user = ret;
                    orgPassword = user.getPassword();
                }
            }
            catch (Exception e) {
                messageBean.alertErrorFailValidation("Saving user failed", e.getMessage());
            }
        }
        else messageBean.alertErrorFailValidation("Saving user failed", "Input fields are invalid");
    }

    /**
     * Action to delete the currently displayed user.
     */
    public void doDeleteUser() {
        this.userService.deleteUser(user);
        user = null;
        orgPassword = null;
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

    private void checkPasswordChange() {
        String password = user.getPassword();
        if (!password.equals(orgPassword)) {
            user.setPassword(passwordEncoder.encode(password));
            // show new plain password once
            messageBean.alertInformation(user.getUsername(), "Password changed to '" + password + "'");
        }
    }
}
