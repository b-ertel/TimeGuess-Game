package at.timeguess.backend.ui.controllers;

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;

import at.timeguess.backend.model.User;
import at.timeguess.backend.services.UserService;

/**
 * Controller for the user profile view.
 */
@Component
@Scope(WebApplicationContext.SCOPE_SESSION)
public class UserProfileController implements Serializable {

    private static final long serialVersionUID = 1L;

    @Autowired
    private UserService userService;

    /**
     * Attribute to cache the currently displayed user
     */
    private User user;

    /**
     * Sets the currently displayed user and reloads it form db.
     * This user is targeted by any further calls of {@link #doReloadUser()}
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
     * Returns a list of all users being team mates of the current user (i.e. belonging to the same teams).
     * @return
     */
    public Collection<User> getTeammates() {
        return userService.getTeammates(user);
    }

    /**
     * Returns the total number of games played by the current user.
     * @return
     */
    public int getTotalGames() {
        return userService.getTotalGames(user);
    }

    /**
     * Returns the total number of games lost by the current user.
     * @return
     */
    public int getTotalGamesLost() {
        return userService.getTotalGamesLost(user);
    }

    /**
     * Returns the total number of games won by the current user.
     * @return
     */
    public int getTotalGamesWon() {
        return userService.getTotalGamesWon(user);
    }

    /**
     * Returns the total number of games won by the current user, grouped by topic.
     * @return
     */
    public Map<String, Integer> getTotalGamesWonByTopic() {
        return userService.getTotalGamesWonByTopic(user);
    }
}
