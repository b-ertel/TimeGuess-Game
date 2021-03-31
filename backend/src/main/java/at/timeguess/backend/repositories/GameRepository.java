package at.timeguess.backend.repositories;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import at.timeguess.backend.model.Game;

public interface GameRepository extends AbstractRepository<Game, Long> {

	int countByTopicId(long topicid);
}
