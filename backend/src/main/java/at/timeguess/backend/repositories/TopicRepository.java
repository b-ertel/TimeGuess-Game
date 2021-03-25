package at.timeguess.backend.repositories;

import at.timeguess.backend.model.Topic;

public interface TopicRepository extends AbstractRepository<Topic, Long> {
	
    String findByName(String name);
    
}
