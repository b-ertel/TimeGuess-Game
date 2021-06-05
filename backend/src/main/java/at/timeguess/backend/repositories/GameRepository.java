package at.timeguess.backend.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import at.timeguess.backend.model.Game;
import at.timeguess.backend.model.GameState;
import at.timeguess.backend.model.User;
import at.timeguess.backend.model.utils.GroupingHelper;

public interface GameRepository extends AbstractRepository<Game, Long> {

    /**
     * to find open games (GameState.VALID_SETUP), active (GameState.PLAYED), finished (GameState.FINISHED)
     * @param  status
     * @return
     */
    @Query("SELECT g FROM Game g WHERE :status =  g.status")
    List<Game> findByStatus(@Param("status") GameState status);

    /**
     * Returns the count of games for the given topic id.
     * @param  topicid
     * @return
     */
    int countByTopicId(long topicid);

    @Query("SELECT g.topic, COUNT(g.topic) AS occ FROM Game g GROUP BY g.topic ORDER BY occ DESC")
    List<Object[]> getTopicAndOccurency();

    /**
     * Returns a list of all games currently not finished (states {@link GameState#SETUP}, {@link GameState#VALID_SETUP},
     * {@link GameState#PLAYED}, {@link GameState#HALTED}).
     * @return
     */
    @Query("SELECT g FROM Game g WHERE g.status IN (0, 1, 2, 3)")
    List<Game> findAllCurrent();

    /**
     * Returns a list of all games the given user is associated to.
     * @return
     */
    @Query("SELECT g FROM Game g JOIN g.teams t JOIN t.team.teamMembers u WHERE u = ?1")
    List<Game> findByUserAll(User user);

    /**
     * Returns a list of all games the given user is associated to, which are currently not finished (states {@link GameState#SETUP}, {@link GameState#VALID_SETUP},
     * {@link GameState#PLAYED}, {@link GameState#HALTED}).
     * @return
     */
    @Query("SELECT g FROM Game g JOIN g.teams t JOIN t.team.teamMembers u WHERE u = ?1 AND g.status IN (0, 1, 2, 3)")
    List<Game> findByUserCurrent(User user);

    /**
     * Checks if given user is participating in given game.
     * @param  user
     * @param  game
     * @return
     */
    @Query("SELECT CASE WHEN COUNT(g) > 0 THEN true ELSE false END FROM Game g JOIN g.teams t JOIN t.team.teamMembers u WHERE u = ?1 AND g = ?2")
    boolean getIsPlayerInGame(User user, Game game);

    /**
     * Returns a map of game ids with maximum points reached.
     * @return
     */
    @Query("SELECT new GroupingHelper(g.game.id, MAX(g.points)) FROM GameTeam g GROUP BY g.game.id")
    List<GroupingHelper> findMaxPointsReached();

    /**
     * Returns a map of team ids with count of game losses.
     * @return
     */
    @Query("SELECT new GroupingHelper(o.team.id, COUNT(*)) FROM GameTeam o JOIN o.game g WHERE g.status IN (4, 5) AND o.points < (SELECT MAX(i.points) FROM GameTeam i WHERE i.game=o.game GROUP BY i.game.id) GROUP BY o.team.id")
    List<GroupingHelper> findLoserTeams();

    /**
     * Returns a map of team ids with count of game wins.
     * @return
     */
    @Query("SELECT new GroupingHelper(o.team.id, COUNT(*)) FROM GameTeam o WHERE (o.game.id, o.points) IN (SELECT g.id, MAX(i.points) FROM GameTeam i JOIN i.game g WHERE g.status IN (4, 5) GROUP BY g.id) GROUP BY o.team.id")
    List<GroupingHelper> findWinnerTeams();

    /**
     * Returns a map of winner team ids, topic names and count of game wins for that topic.
     * @return
     */
    @Query("SELECT new GroupingHelper(o.team.id, t.name, COUNT(*)) FROM GameTeam o JOIN o.game g JOIN g.topic t WHERE g.status IN (4, 5) AND (g.id, o.points) IN (SELECT i.game.id, MAX(i.points) FROM GameTeam i GROUP BY i.game.id) GROUP BY o.team.id, t.name")
    List<GroupingHelper> findWinnerTeamsByTopic();
}
