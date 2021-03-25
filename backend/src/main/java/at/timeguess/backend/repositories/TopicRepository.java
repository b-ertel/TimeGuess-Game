package at.timeguess.backend.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import at.timeguess.backend.model.Topic;

public interface TopicRepository extends AbstractRepository<Round, Long> {
	
    String findByName(String name);
    
}
