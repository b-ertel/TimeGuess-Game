package at.timeguess.backend.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import at.timeguess.backend.model.Round;

public interface RoundRepository extends AbstractRepository<Round, Long> {
	
	
	List<Round> findByGuessingUser(String username);
	
	//TODO: Query to find Terms
	
	//TODO: Query to find Terms guessed correct
	@Query("SELECT COUNT(topic) FROM Round r JOIN Term t WHERE r.termToGuess = t.id AND r.correctAnswer=true AND topic=:topicid")
	int getNrCorrectAnswerByTopic(@Param("topicid") long topicid);
	
	
	//TODO: Query to find correct Answers from certain User
}
