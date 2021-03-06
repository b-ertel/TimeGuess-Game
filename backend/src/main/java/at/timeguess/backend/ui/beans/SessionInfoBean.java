package at.timeguess.backend.ui.beans;

import java.io.Serializable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import at.timeguess.backend.model.User;
import at.timeguess.backend.model.UserRole;
import at.timeguess.backend.services.UserService;

/**
 * Session information bean to retrieve session-specific parameters.
 */
@Component
@Scope("session")
public class SessionInfoBean implements Serializable {

    private static final long serialVersionUID = 1L;

    @Autowired
    private UserService userService;

    /**
     * Attribute to cache the current user.
     */
    private User currentUser;

    /**
     * Returns the currently logged on user, null if no user is authenticated for this session.
     * @return user
     */
    public User getCurrentUser() {
        if (currentUser == null) {
            String currentUserName = getCurrentUserName();
            if (currentUserName.isEmpty()) {
                return null;
            }
            currentUser = userService.loadUser(currentUserName);
        }
        return currentUser;
    }

    /**
     * Returns the username of the user for this session,
     * empty string if no user has been authenticated for this session.
     * @return user name
     */
    public String getCurrentUserName() {
        if (!isLoggedIn()) {
            return "";
        }
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String name = auth.getName(); // get logged in username
        return name;
    }

    /**
     * Returns the roles of the user for this session as space-separated list,
     * empty string if no user has been authenticated for this session-
     * @return user roles
     */
    public String getCurrentUserRoles() {
        if (!isLoggedIn()) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        for (GrantedAuthority role : auth.getAuthorities()) {
            sb.append(role.getAuthority());
            sb.append(' ');
        }
        return sb.toString().trim();
    }

    /**
     * Checks if a user is authenticated for this session.
     * @return true if a non-anonymous user has been authenticated, false otherwise.
     */
    public boolean isLoggedIn() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            return auth.isAuthenticated() && !auth.getName().startsWith("anonymous");
        }
        else {
            return false;
        }
    }

    /**
     * Checks if the user for this session has the given role (c.f. {@link UserRole}).
     * @param  role the role to check for as string
     * @return true if a user is authenticated and the current user has the given role, false otherwise
     */
    public boolean hasRole(String role) {
        if (role == null || role.isEmpty() || !isLoggedIn()) {
            return false;
        }
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        for (GrantedAuthority ga : auth.getAuthorities()) {
            if (role.equals(ga.getAuthority())) {
                return true;
            }
        }
        return false;
    }
}
