package at.timeguess.backend.services;

import java.util.Collection;
import java.util.Date;

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
import at.timeguess.backend.model.util.GroupingHelper;
import at.timeguess.backend.repositories.GameRepository;
import at.timeguess.backend.repositories.UserRepository;

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
     * Returns the total number of games won by the given user.
     * @return
     */
    public int getTotalGamesWon(User user) {
        GroupingHelper.List winners = new GroupingHelper.List(gameRepository.findWinnerTeams());
        return winners.getSumForIds(userRepository.findAllTeamsIn(user, winners.getIds()));
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
     * @return the updated user
     */
    @PreAuthorize("hasAuthority('ADMIN')")
    public User saveUser(User user) {
        if (user.isNew()) {
            user.setCreateDate(new Date());
            user.setCreateUser(getAuthenticatedUser());
        } else {
            user.setUpdateDate(new Date());
            user.setUpdateUser(getAuthenticatedUser());
        }
        return userRepository.save(user);
    }

    /**
     * Deletes the user.
     * @param user the user to delete
     */
    @PreAuthorize("hasAuthority('ADMIN')")
    public void deleteUser(User user) {
        userRepository.delete(user);

        User authUser = getAuthenticatedUser();
        LOGGER.info("User {} '{}' was deleted by User {} '{}'", user.getId(), user.getUsername(), authUser.getId(),
                authUser.getUsername());
    }

    private User getAuthenticatedUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return userRepository.findFirstByUsername(auth.getName());
    }
}
