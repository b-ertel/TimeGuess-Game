package at.timeguess.backend.repositories;

import at.timeguess.backend.model.Topic;

public interface TopicRepository extends AbstractRepository<Topic, Long> {
	
    Topic findByName(String name);
    
}
