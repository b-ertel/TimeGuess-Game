package at.timeguess.backend.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import at.timeguess.backend.model.Round;

public interface RoundRepository extends AbstractRepository<Round, Long> {
	
	@Query("SELECT r FROM Round r WHERE r.user_id = :userid")
	List<Round> findByUserId(@Param("userid") Long id);
	
	//TODO: Query to find Terms
	
	//TODO: Query to find Terms guessed correct
	
	//TODO: Query to find correct Answers from certain User
}
