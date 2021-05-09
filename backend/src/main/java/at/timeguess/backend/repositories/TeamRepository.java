package at.timeguess.backend.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.Query;

import at.timeguess.backend.model.Game;
import at.timeguess.backend.model.Team;

public interface TeamRepository extends AbstractRepository<Team, Long> {

    @Query("SELECT t FROM Team t WHERE t.name = ?1")
    Team findFirstByName(String name);

    /**
     * Returns a list of teams currently not playing.
     * @return
     */
    @Query("SELECT r FROM Team r WHERE r NOT IN (SELECT t FROM Team t JOIN t.games tg JOIN tg.game g WHERE g.status IN (0, 1, 2, 3))")
    List<Team> findAvailableTeams();

    /**
     * Checks if the given team is currently playing or not.
     * @param team
     * @return
     */
    @Query("SELECT CASE WHEN COUNT(r) > 0 THEN true ELSE false END FROM Team r WHERE r = ?1 AND r NOT IN (SELECT t FROM Team t JOIN t.games tg JOIN tg.game g WHERE g.status IN (0, 1, 2, 3))")
    boolean getIsAvailableTeam(Team team);

    /**
     * Checks if the given team is currently playing in any other than the given game or not.
     * @param team
     * @param game
     * @return
     */
    @Query("SELECT CASE WHEN COUNT(r) > 0 THEN true ELSE false END FROM Team r WHERE r = ?1 AND r NOT IN (SELECT t FROM Team t JOIN t.games tg JOIN tg.game g WHERE NOT g IS ?2 AND g.status IN (0, 1, 2, 3))")
    boolean getIsAvailableTeamForGame(Team team, Game game);
}
