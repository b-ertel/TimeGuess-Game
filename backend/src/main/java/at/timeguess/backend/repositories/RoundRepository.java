package at.timeguess.backend.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import at.timeguess.backend.model.Game;
import at.timeguess.backend.model.Round;
import at.timeguess.backend.model.RoundId;
import at.timeguess.backend.model.Team;
import at.timeguess.backend.model.User;

/**
 * Repository for managing {@link Round} entities.
 */
public interface RoundRepository extends AbstractRepository<Round, RoundId> {

    List<Round> findByGuessingUser(String username);

    @Query("SELECT COUNT(topic) FROM Round r JOIN Term t ON (r.termToGuess = t.id) WHERE r.correctAnswer=true AND topic=:topicid")
    int getNrCorrectAnswerByTopic(@Param("topicid") long topicid);

    @Query("SELECT COUNT(topic) FROM Round r JOIN Term t ON (r.termToGuess = t.id) WHERE r.correctAnswer=false AND topic=:topicid")
    int getNrIncorrectAnswerByTopic(@Param("topicid") long topicid);

    @Query("SELECT COUNT(r.guessingUser) FROM Round r JOIN r.guessingUser WHERE r.correctAnswer=true AND r.guessingUser=:user")
    int getNrOfCorrectAnswerByUser(@Param("user") User user);

    @Query("SELECT COUNT(r.guessingUser) FROM Round r JOIN r.guessingUser WHERE r.correctAnswer=false AND r.guessingUser=:user")
    int getNrOfIncorrectAnswerByUser(@Param("user") User user);

    @Query("SELECT r FROM Round r WHERE r.game=:game")
    List<Round> getRoundOfGame(@Param("game") Game game);

    @Query("SELECT r FROM Round r WHERE r.game=:game AND r.guessingTeam=:team")
    List<Round> getRoundOfGameWithTeam(@Param("game") Game game, @Param("team") Team team);

    @Query("SELECT SUM(r.points) FROM Round r WHERE r.game=:game AND r.guessingTeam=:team")
    Integer getPointsOfTeamInGame(@Param("game") Game game, @Param("team") Team team);
}
