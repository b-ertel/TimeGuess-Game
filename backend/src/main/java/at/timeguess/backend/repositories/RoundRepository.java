package at.timeguess.backend.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import at.timeguess.backend.model.Game;
import at.timeguess.backend.model.Round;
import at.timeguess.backend.model.Team;
import at.timeguess.backend.model.User;

public interface RoundRepository extends AbstractRepository<Round, Long> {
	
	
	List<Round> findByGuessingUser(String username);
	
	@Query("SELECT COUNT(topic) FROM Round r JOIN Term t ON (r.termToGuess = t.id) WHERE r.correctAnswer=true AND topic=:topicid")
	int getNrCorrectAnswerByTopic(@Param("topicid") long topicid);
	
	@Query("SELECT COUNT(topic) FROM Round r JOIN Term t ON (r.termToGuess = t.id) WHERE r.correctAnswer=false AND topic=:topicid")
	int getNrIncorrectAnswerByTopic(@Param("topicid") long topicid);
	
	@Query("SELECT COUNT(guessingUser) FROM Round r WHERE r.correctAnswer=true AND guessingUser=:user")
	int getNrOfCorrectAnswerByUser(@Param("user") User user);
	
	@Query("SELECT COUNT(guessingUser) FROM Round r WHERE r.correctAnswer=false AND guessingUser=:user")
	int getNrOfIncorrectAnswerByUser(@Param("user") User user);
	
	@Query("SELECT r FROM Round r WHERE r.game=:game")
	List<Round> getRoundOfGame(@Param("game") Game game);
	
	@Query("SELECT r FROM Round r WHERE r.game=:game AND r.guessingTeam=:team")
	List<Round> getRoundOfGameWithTeam(@Param("game") Game game, @Param("team") Team team);
	
	@Query("SELECT SUM(r.points) FROM Round r WHERE r.game=:game AND r.guessingTeam=:team")
	Integer getPointsOfTeamInGame(@Param("game") Game game, @Param("team") Team team);
}
