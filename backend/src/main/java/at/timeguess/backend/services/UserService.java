package at.timeguess.backend.services;

import java.util.Collection;
import java.util.Date;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import at.timeguess.backend.model.User;
import at.timeguess.backend.model.UserRole;
import at.timeguess.backend.model.utils.GroupingHelper;
import at.timeguess.backend.repositories.GameRepository;
import at.timeguess.backend.repositories.UserRepository;
import at.timeguess.backend.ui.beans.MessageBean;

/**
 * Service for accessing and manipulating user data.
 */
@Component
@Scope("application")
public class UserService {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private MessageBean messageBean;

    /**
     * Returns a collection of all users.
     * @return
     */
    @PreAuthorize("hasAuthority('ADMIN')")
    public Collection<User> getAllUsers() {
        return userRepository.findAll();
    }

    /**
     * Returns a collection of all users with role 'PLAYER'.
     * @return
     */
    public Collection<User> getAllPlayers() {
        return userRepository.findByRole(UserRole.PLAYER);
    }

    /**
     * Returns a list of all users being team mates of the given user (i.e. belonging to the same teams).
     * @return
     */
    public Collection<User> getTeammates(User user) {
        return userRepository.findByTeams(user);
    }

    /**
     * Returns the total number of games played by the given user.
     * @return
     */
    public int getTotalGames(User user) {
        return userRepository.getTotalGames(user);
    }

    /**
     * Returns the total number of games lost by the given user.
     * @return
     */
    public int getTotalGamesLost(User user) {
        GroupingHelper.List losers = new GroupingHelper.List(gameRepository.findLoserTeams());
        return losers.getSumForIds(userRepository.findAllTeamsIn(user, losers.getIds()));
    }

    /**
     * Returns the total number of games won by the given user.
     * @return
     */
    public int getTotalGamesWon(User user) {
        GroupingHelper.List winners = new GroupingHelper.List(gameRepository.findWinnerTeams());
        return winners.getSumForIds(userRepository.findAllTeamsIn(user, winners.getIds()));
    }

    /**
     * Returns the total number of games won by the given user, split up by topic.
     * @return
     */
    public Map<String, Integer> getTotalGamesWonByTopic(User user) {
        GroupingHelper.List winners = new GroupingHelper.List(gameRepository.findWinnerTeamsByTopic());
        return winners.getSumForIdsGroupedByName(userRepository.findAllTeamsIn(user, winners.getIds()));
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
     * The user requesting this operation will also be stored as {@link User#createDate} or {@link User#updateUser} respectively.
     * @param user the user to save
     * @return the saved user
     */
    @PreAuthorize("hasAuthority('ADMIN')")
    public User saveUser(User user) {
        User auth = getAuthenticatedUser();
        if (auth == null) auth = user;

        boolean isNew = user.isNew();
        if (isNew) {
            user.setCreateDate(new Date());
            user.setCreateUser(auth);
        }
        else {
            user.setUpdateDate(new Date());
            user.setUpdateUser(auth);
        }
        User ret = userRepository.save(user);

        // show ui message and log
        messageBean.alertInformation(ret.getUsername(), isNew ? "New user created" : "User updated");

        LOGGER.info("User '{}' (id={}) was {} by User '{}' (id={})", ret.getUsername(), ret.getId(),
                isNew ? "created" : "updated", auth.getUsername(), auth.getId());

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

    User getAuthenticatedUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return userRepository.findFirstByUsername(auth.getName());
    }
}
