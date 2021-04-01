package at.timeguess.backend.repositories;

import org.springframework.data.jpa.repository.Query;

import at.timeguess.backend.model.Topic;

public interface TopicRepository extends AbstractRepository<Topic, Long> {
	
    String findByName(String name);
    
    @Query("SELECT COUNT(id) FROM Topic")
    int nrOfTopics();
}
