package at.timeguess.backend.services;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.hibernate.annotations.Target;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.dao.DataIntegrityViolationException;
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
import at.timeguess.backend.spring.CDIAwareBeanPostProcessor;
import at.timeguess.backend.ui.beans.MessageBean;
import at.timeguess.backend.ui.beans.NewUserBean;
import at.timeguess.backend.ui.websockets.WebSocketManager;
import at.timeguess.backend.utils.CDIAutowired;
import at.timeguess.backend.utils.CDIContextRelated;

/**
 * Service for accessing and manipulating user data.
 */
@Component
@Scope(WebApplicationContext.SCOPE_APPLICATION)
@CDIContextRelated
public class UserService {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private MessageBean messageBean;

    @CDIAutowired
    private WebSocketManager websocketManager;

    /**
     * @apiNote neither {@link Autowired} nor {@link CDIAutowired} work for a {@link Component},
     * and {@link javax.annotation.PostConstruct} is not invoked, so autowiring is done manually
     */
    public UserService() {
        if (websocketManager == null) {
            new CDIAwareBeanPostProcessor().postProcessAfterInitialization(this, "websocketManager");
        }
    }

    /**
     * Returns a list of all users.
     * @return list of users
     */
    @PreAuthorize("hasAuthority('ADMIN')")
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    /**
     * Returns a list of all users with role 'PLAYER'.
     * @return list of users
     */
    @PreAuthorize("hasAuthority('PLAYER')")
    public List<User> getAllPlayers() {
        return userRepository.findByRole(UserRole.PLAYER);
    }

    /**
     * Returns a list of all users with role 'PLAYER', which are not currently playing.
     * @return list of users
     */
    @PreAuthorize("hasAuthority('PLAYER')")
    public List<User> getAvailablePlayers() {
        return userRepository.findAvailablePlayers();
    }

    /**
     * Returns a list of all users being team mates of the given user (i.e. belonging to the same teams).
     * @param user user
     * @return list of users
     */
    @PreAuthorize("hasAuthority('PLAYER')")
    public List<User> getTeammates(User user) {
        return userRepository.findByTeams(user);
    }

    /**
     * Returns the total number of games played by the given user.
     * @param user user
     * @return count of games
     */
    @PreAuthorize("hasAuthority('PLAYER')")
    public int getTotalGames(User user) {
        return userRepository.getTotalGames(user);
    }

    /**
     * Returns the total number of games lost by the given user.
     * @param user user
     * @return count of games
     */
    @PreAuthorize("hasAuthority('PLAYER')")
    public int getTotalGamesLost(User user) {
        GroupingHelper.List losers = new GroupingHelper.List(gameRepository.findLoserTeams());
        return losers.getSumForIds(userRepository.findAllTeamsIn(user, losers.getIds()));
    }

    /**
     * Returns the total number of games won by the given user.
     * @param user user
     * @return count of games
     */
    @PreAuthorize("hasAuthority('PLAYER')")
    public int getTotalGamesWon(User user) {
        GroupingHelper.List winners = new GroupingHelper.List(gameRepository.findWinnerTeams());
        return winners.getSumForIds(userRepository.findAllTeamsIn(user, winners.getIds()));
    }

    /**
     * Returns the total number of games won by the given user, split up by topic.
     * @param user user
     * @return count of games split by topic
     */
    @PreAuthorize("hasAuthority('PLAYER')")
    public Map<String, Integer> getTotalGamesWonByTopic(User user) {
        GroupingHelper.List winners = new GroupingHelper.List(gameRepository.findWinnerTeamsByTopic());
        return winners.getSumForIdsGroupedByName(userRepository.findAllTeamsIn(user, winners.getIds()));
    }

    /**
     * Checks if a user with the given username already exists.
     * @param username user name
     * @return true if the given username is already saved in the database, false otherwise.
     */
    @Target(NewUserBean.class)
    public boolean hasUser(String username) {
        return userRepository.getTotalByUsername(username) > 0;
    }

    /**
     * Checks if the given user is currently playing or not.
     * @param user user
     * @return true if he is, false if not
     */
    @PreAuthorize("hasAuthority('PLAYER')")
    public boolean isAvailablePlayer(User user) {
        return userRepository.getIsAvailablePlayer(user);
    }

    /**
     * Loads a single user identified by its id.
     * @param user the user to search for
     * @return the user with the given id
     */
    @PreAuthorize("hasAuthority('ADMIN') OR hasAuthority('PLAYER') OR principal.username eq #user.username")
    public User loadUser(User user) {
        return userRepository.findById(user.getId()).orElse(null);
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
     * This method will also set {@link User#setCreateDate(Date)} for new entities or {@link User#setUpdateDate(Date)} for updated entities.
     * The user requesting this operation will also be stored as {@link User#setCreateUser(User)} or {@link User#setUpdateUser(User)} respectively.
     * Additionally fills gui message with success or failure info and triggers a push update.
     * @param user the user to save
     * @return the saved user
     * @apiNote Message handling ist done here, because this is the central place for saving users.
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

            // fill ui message, send update and log
            messageBean.alertInformation(ret.getUsername(), isNew ? "New user created" : "User updated");

            if (websocketManager != null)
                websocketManager.getUserRegistrationChannel().send(
                    Map.of("type", "userUpdate", "name", user.getUsername(), "id", user.getId()));

            if (auth == null) auth = ret;
            LOGGER.info("User '{}' (id={}) was {} by User '{}' (id={})", ret.getUsername(), ret.getId(),
                isNew ? "created" : "updated", auth.getUsername(), auth.getId());
        }
        catch (Exception e) {
            String msg = "Saving user failed";
            if (e.getMessage().contains("USER(USERNAME)"))
                msg += String.format(": user named '%s' already exists", user.getUsername());
            messageBean.alertErrorFailValidation(user.getUsername(), msg);

            LOGGER.info("Saving user '{}' (id={}) failed, stack trace:", user.getUsername(), user.getId());
            e.printStackTrace();
        }
        return ret;
    }

    /**
     * Deletes the user.
     * Additionally fills gui message with success or failure info and triggers a push update.
     * @param user the user to delete
     */
    @PreAuthorize("hasAuthority('ADMIN')")
    public void deleteUser(User user) {
        try {
            userRepository.delete(user);

            // very weird behaviour: the following kind of exception should be thrown
            // but instead just nothing happens... so do-it-yourself
            if (this.hasUser(user.getUsername()))
                throw new DataIntegrityViolationException("Delete failed without throwing an exception");

            // fill ui message, send update and log
            messageBean.alertInformation(user.getUsername(), "User was deleted");

            if (websocketManager != null)
                websocketManager.getUserRegistrationChannel().send(
                    Map.of("type", "userUpdate", "name", user.getUsername(), "id", user.getId()));

            User auth = getAuthenticatedUser();
            LOGGER.info("User '{}' (id={}) was deleted by User '{}' (id={})", user.getUsername(), user.getId(),
                auth.getUsername(), auth.getId());
        }
        catch (Exception e) {
            messageBean.alertErrorFailValidation(user.getUsername(), "Deleting user failed");
            LOGGER.info("Deleting user '{}' (id={}) failed, stack trace:", user.getUsername(), user.getId());
            e.printStackTrace();
        }
    }

    /**
     * Returns the user representing the currently authenticated principal.
     * @return user
     */
    User getAuthenticatedUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findFirstByUsername(auth.getName());
        if (user == null && !auth.getName().startsWith("anonymous")) {
            user = new User();
            user.setUsername(auth.getName());
        }
        return user;
    }

    private User getAdminUser() {
        List<User> admins = userRepository.findByRole(UserRole.ADMIN);
        return admins.size() > 0 ? admins.get(0) : null;
    }
}
