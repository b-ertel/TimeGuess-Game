package at.timeguess.backend.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import at.timeguess.backend.model.Round;

public interface RoundRepository extends AbstractRepository<Round, Long> {
	
	
	List<Round> findByGuessingUser(String username);
	
	//TODO: Query to find Terms
	
	@Query("SELECT COUNT(topic) FROM Round r JOIN Term t ON (r.termToGuess = t.id) WHERE r.correctAnswer=true AND topic=:topicid")
	int getNrCorrectAnswerByTopic(@Param("topicid") long topicid);
	
	@Query("SELECT COUNT(topic) FROM Round r JOIN Term t ON (r.termToGuess = t.id) WHERE r.correctAnswer=false AND topic=:topicid")
	int getNrIncorrectAnswerByTopic(@Param("topicid") long topicid);
	//TODO: Query to find correct Answers from certain User
}
