package at.timeguess.backend.services;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.hibernate.annotations.Target;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;

import at.timeguess.backend.model.User;
import at.timeguess.backend.model.UserRole;
import at.timeguess.backend.model.utils.GroupingHelper;
import at.timeguess.backend.repositories.GameRepository;
import at.timeguess.backend.repositories.UserRepository;
import at.timeguess.backend.ui.beans.MessageBean;
import at.timeguess.backend.ui.beans.NewUserBean;

/**
 * Service for accessing and manipulating user data.
 */
@Component
@Scope(WebApplicationContext.SCOPE_APPLICATION)
public class UserService {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private MessageBean messageBean;

    /**
     * Returns a list of all users.
     * @return
     */
    @PreAuthorize("hasAuthority('ADMIN')")
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    /**
     * Returns a list of all users with role 'PLAYER'.
     * @return
     */
    @PreAuthorize("hasAuthority('PLAYER')")
    public List<User> getAllPlayers() {
        return userRepository.findByRole(UserRole.PLAYER);
    }

    /**
     * Returns a list of all users with role 'PLAYER', which are not currently playing.
     * @return
     */
    @PreAuthorize("hasAuthority('PLAYER')")
    public List<User> getAvailablePlayers() {
        return userRepository.findAvailablePlayers();
    }

    /**
     * Returns a list of all users being team mates of the given user (i.e. belonging to the same teams).
     * @return
     */
    @PreAuthorize("hasAuthority('PLAYER')")
    public List<User> getTeammates(User user) {
        return userRepository.findByTeams(user);
    }

    /**
     * Returns the total number of games played by the given user.
     * @return
     */
    @PreAuthorize("hasAuthority('PLAYER')")
    public int getTotalGames(User user) {
        return userRepository.getTotalGames(user);
    }

    /**
     * Returns the total number of games lost by the given user.
     * @return
     */
    @PreAuthorize("hasAuthority('PLAYER')")
    public int getTotalGamesLost(User user) {
        GroupingHelper.List losers = new GroupingHelper.List(gameRepository.findLoserTeams());
        return losers.getSumForIds(userRepository.findAllTeamsIn(user, losers.getIds()));
    }

    /**
     * Returns the total number of games won by the given user.
     * @return
     */
    @PreAuthorize("hasAuthority('PLAYER')")
    public int getTotalGamesWon(User user) {
        GroupingHelper.List winners = new GroupingHelper.List(gameRepository.findWinnerTeams());
        return winners.getSumForIds(userRepository.findAllTeamsIn(user, winners.getIds()));
    }

    /**
     * Returns the total number of games won by the given user, split up by topic.
     * @return
     */
    @PreAuthorize("hasAuthority('PLAYER')")
    public Map<String, Integer> getTotalGamesWonByTopic(User user) {
        GroupingHelper.List winners = new GroupingHelper.List(gameRepository.findWinnerTeamsByTopic());
        return winners.getSumForIdsGroupedByName(userRepository.findAllTeamsIn(user, winners.getIds()));
    }

    /**
     * Checks if a user with the given username already exists.
     * @param username
     * @return true if the given username is already saved in the database, false otherwise.
     */
    @Target(NewUserBean.class)
    public boolean hasUser(String username) {
        return userRepository.getTotalByUsername(username) > 0;
    }

    /**
     * Checks if the given user is currently playing or not.
     * @return
     */
    @PreAuthorize("hasAuthority('PLAYER')")
    public boolean isAvailablePlayer(User user) {
        return userRepository.getIsAvailablePlayer(user);
    }

    /**
     * Loads a single user identified by its id.
     * @param id the id to search for
     * @return the user with the given id
     */
    @PreAuthorize("hasAuthority('ADMIN') OR hasAuthority('PLAYER') OR principal.username eq #username")
    public User loadUser(Long id) {
        return userRepository.findById(id).get();
    }

    /**
     * Loads a single user identified by its username.
     * @param username the username to search for
     * @return the user with the given username
     */
    @PreAuthorize("hasAuthority('ADMIN') OR hasAuthority('PLAYER') OR principal.username eq #username")
    public User loadUser(String username) {
        return userRepository.findFirstByUsername(username);
    }

    /**
     * Saves the user.
     * This method will also set {@link User#createDate} for new entities or {@link User#updateDate} for updated entities.
     * The user requesting this operation will also be stored as {@link User#createUser} or {@link User#updateUser} respectively.
     * @param user the user to save
     * @return the saved user
     */
    @Target(NewUserBean.class)
    @PreAuthorize("hasAuthority('ADMIN') OR #user.id == null OR principal.username eq #user.username")
    public User saveUser(User user) {
        User ret = null;
        try {
            // for self registration get any admin user as creator
            // null would work also (setting the property to optional in User class)
            // but demo.UserStatusController.afterStatusChange throws an exception then
            User auth = getAuthenticatedUser();
            if (auth == null) auth = this.getAdminUser();

            boolean isNew = user.isNew();
            if (isNew) {
                user.setCreateDate(new Date());
                user.setCreateUser(auth);
            }
            else {
                user.setUpdateDate(new Date());
                user.setUpdateUser(auth);
            }
            ret = userRepository.save(user);

            // show ui message and log
            messageBean.alertInformation(ret.getUsername(), isNew ? "New user created" : "User updated");

            if (auth == null) auth = ret;
            LOGGER.info("User '{}' (id={}) was {} by User '{}' (id={})", ret.getUsername(), ret.getId(),
                    isNew ? "created" : "updated", auth.getUsername(), auth.getId());
        }
        catch (Exception e) {
            String msg = "Saving user failed";
            if (e.getMessage().contains("USER(USERNAME)"))
                msg += String.format(": user named '%s' already exists", user.getUsername());
            messageBean.alertError(user.getUsername(), msg);

            LOGGER.info("Saving user '{}' (id={}) failed, stack trace:", user.getUsername(), user.getId());
            e.printStackTrace();
        }
        return ret;
    }

    /**
     * Deletes the user.
     * @param user the user to delete
     */
    @PreAuthorize("hasAuthority('ADMIN')")
    public void deleteUser(User user) {
        try {
            userRepository.delete(user);

            // show ui message and log
            messageBean.alertInformation(user.getUsername(), "User was deleted");

            User auth = getAuthenticatedUser();
            LOGGER.info("User '{}' (id={}) was deleted by User '{}' (id={})", user.getUsername(), user.getId(),
                    auth.getUsername(), auth.getId());
        }
        catch (Exception e) {
            messageBean.alertError(user.getUsername(), "Deleting user failed");
            LOGGER.info("Deleting user '{}' (id={}) failed, stack trace:", user.getUsername(), user.getId());
            e.printStackTrace();
        }
    }

    /**
     * Returns the user representing the currently authenticated principal.
     * @return
     */
    User getAuthenticatedUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return userRepository.findFirstByUsername(auth.getName());
    }

    private User getAdminUser() {
        List<User> admins = userRepository.findByRole(UserRole.ADMIN);
        return admins.size() > 0 ? admins.get(0) : null;
    }
}
