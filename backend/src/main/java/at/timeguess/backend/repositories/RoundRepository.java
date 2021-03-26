package at.timeguess.backend.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import at.timeguess.backend.model.Round;

public interface RoundRepository extends AbstractRepository<Round, Long> {
	
	
	List<Round> findByUsername(String username);
	
	//TODO: Query to find Terms
	
	//TODO: Query to find Terms guessed correct
	
	//TODO: Query to find correct Answers from certain User
}
