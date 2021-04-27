package at.timeguess.backend.repositories;

import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import at.timeguess.backend.model.User;
import at.timeguess.backend.model.UserRole;

/**
 * Repository for managing {@link User} entities.
 */
public interface UserRepository extends AbstractRepository<User, Long> {

    User findFirstByUsername(String username);

    /**
     * Returns a collection of all users with role 'PLAYER'.
     * @return
     */
    @Query("SELECT u FROM User u WHERE :role MEMBER OF u.roles")
    List<User> findByRole(@Param("role") UserRole role);

    /**
     * Returns a list of all users being team mates of the given user (i.e. belonging to the same teams).
     * @return
     */
    @Query("SELECT DISTINCT m FROM User m JOIN m.teams tm WHERE tm IN (SELECT tu FROM User u JOIN u.teams tu WHERE u = ?1) AND m <> ?1")
    List<User> findByTeams(User user);

    /**
     * Returns a list of team ids matching the given user and the given team ids.
     * @return
     */
    @Query("SELECT t.id FROM User u JOIN u.teams t WHERE u = ?1 AND t.id IN ?2")
    List<Long> findAllTeamsIn(User user, Set<Long> teams);

    /**
     * Returns a list of all users with role 'PLAYER', which are not currently playing.
     * @return
     */
    @Query("SELECT r FROM User r WHERE 'PLAYER' MEMBER OF r.roles AND r NOT IN (SELECT u FROM User u JOIN u.teams t JOIN t.games tg JOIN tg.game g WHERE g.status IN (0, 1, 2, 3))")
    List<User> findAvailablePlayers();

    /**
     * Checks if the given user is currently playing or not.
     * @return
     */
    @Query("SELECT CASE WHEN COUNT(r) > 0 THEN true ELSE false END FROM User r WHERE r = ?1 AND 'PLAYER' MEMBER OF r.roles AND r NOT IN (SELECT u FROM User u JOIN u.teams t JOIN t.games tg JOIN tg.game g WHERE g.status IN (0, 1, 2, 3))")
    boolean getIsAvailablePlayer(User user);

    /**
     * Returns the total number of games played by the given user.
     * @return
     */
    @Query("SELECT COUNT(g) FROM User u JOIN u.teams t JOIN t.games g WHERE u = ?1")
    int getTotalGames(User user);

    /**
     * Returns the count of users with the given username.
     * @param username
     * @return
     */
    @Query("SELECT COUNT(u) FROM User u WHERE u.username = ?1")
    int getTotalByUsername(String username);
}
