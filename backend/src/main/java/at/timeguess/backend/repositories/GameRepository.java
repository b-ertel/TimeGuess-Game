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
     * @param status
     * @return
     */
    @Query("SELECT g FROM Game g WHERE :status =  g.status")
    List<Game> findByStatus(@Param("status") GameState status);

    int countByTopicId(long topicid);
    
    @Query("SELECT g.topic, COUNT(g.topic) AS occ FROM Game g GROUP BY g.topic ORDER BY occ DESC")
    List<Object[]> getTopicAndOccurency();

    @Query("SELECT g FROM Game g WHERE g.status IN (0, 1)")
    List<Game> findAllCurrent();

    @Query("SELECT g FROM Game g JOIN g.teams t JOIN t.team.teamMembers u WHERE u = ?1")
    List<Game> findByUserAll(User user);

    @Query("SELECT g FROM Game g JOIN g.teams t JOIN t.team.teamMembers u WHERE u = ?1 AND g.status IN (0, 1)")
    List<Game> findByUserCurrent(User user);

    /**
     * Checks if given user is in teams for given game + game is setup (and not started yet) + participation is not confirmed yet
     * @param user
     * @param game
     * @return
     */
    @Query("SELECT COUNT(g) FROM Game g JOIN g.teams t JOIN t.team.teamMembers u WHERE u = ?1 AND g = ?2 AND g.status IN (0, 1) AND ?1 NOT MEMBER OF g.confirmedUsers")
    Long findByUserConfirmation(User user, Game game);

    @Query("SELECT new GroupingHelper(g.game.id, MAX(g.points)) FROM GameTeam g GROUP BY g.game.id")
    List<GroupingHelper> findMaxPointsReached();

    @Query("SELECT new GroupingHelper(o.team.id, COUNT(*)) FROM GameTeam o WHERE o.points < (SELECT MAX(i.points) FROM GameTeam i WHERE i.game=o.game GROUP BY i.game) GROUP BY o.team.id")
    List<GroupingHelper> findLoserTeams();

    @Query("SELECT new GroupingHelper(o.team.id, COUNT(*)) FROM GameTeam o WHERE (o.game.id, o.points) IN (SELECT i.game.id, MAX(i.points) FROM GameTeam i GROUP BY i.game.id) GROUP BY o.team.id")
    List<GroupingHelper> findWinnerTeams();

    @Query("SELECT new GroupingHelper(o.team.id, g.topic.name, COUNT(*)) FROM GameTeam o JOIN Game g ON g.id=o.game.id WHERE (o.game.id, o.points) IN (SELECT i.game.id, MAX(i.points) FROM GameTeam i GROUP BY i.game.id) GROUP BY o.team.id, g.topic.name")
    List<GroupingHelper> findWinnerTeamsByTopic();
}
