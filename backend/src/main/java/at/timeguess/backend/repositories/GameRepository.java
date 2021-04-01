package at.timeguess.backend.repositories;

import at.timeguess.backend.model.Game;

public interface GameRepository extends AbstractRepository<Game, Long> {

	int countByTopicId(long topicid);
	
}
