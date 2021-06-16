package at.timeguess.backend.repositories;

import org.springframework.data.jpa.repository.Query;

import at.timeguess.backend.model.Topic;

/**
 * Repository for managing {@link Topic} entities.
 */
public interface TopicRepository extends AbstractRepository<Topic, Long> {

    Topic findByName(String name);

    @Query("SELECT COUNT(id) FROM Topic")
    int nrOfTopics();
}
