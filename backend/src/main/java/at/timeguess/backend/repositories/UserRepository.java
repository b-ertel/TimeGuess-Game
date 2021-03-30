package at.timeguess.backend.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import at.timeguess.backend.model.User;
import at.timeguess.backend.model.UserRole;

/**
 * Repository for managing {@link User} entities.
 *
 */
public interface UserRepository extends AbstractRepository<User, String> {

    User findFirstByUsername(String username);

    List<User> findByUsernameContaining(String username);

    @Query("SELECT u FROM User u WHERE CONCAT(u.firstName, ' ', u.lastName) = :wholeName")
    List<User> findByWholeNameConcat(@Param("wholeName") String wholeName);

    @Query("SELECT u FROM User u WHERE :role MEMBER OF u.roles")
    List<User> findByRole(@Param("role") UserRole role);
	
    @Query("SELECT m FROM User m JOIN m.teams tm WHERE tm IN (SELECT tu FROM User u JOIN u.teams tu WHERE u = ?1) AND m <> ?1")
    List<User> findByTeams(User user);
}
