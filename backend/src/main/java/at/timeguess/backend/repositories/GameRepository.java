package at.timeguess.backend.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.Query;

import at.timeguess.backend.model.Game;

public interface GameRepository extends AbstractRepository<Game, Long> {

	int countByTopicId(long topicid);
	
	@Query("SELECT g.topic, COUNT(g.topic) AS occ FROM Game g GROUP BY g.topic ORDER BY occ DESC")
	List<Object[]> getTopicAndOccurency();
}
