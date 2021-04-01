package at.timeguess.backend.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.Query;

import at.timeguess.backend.model.Term;
import at.timeguess.backend.model.Topic;

public interface TermRepository extends AbstractRepository<Term, Long> {
	
    List<Term> findByTopic(Topic topic);
    Term findById(Long id);

    @Query("SELECT t FROM Term t WHERE t.name=:name AND t.topic=:topic")
    Term findByName(String name, Topic topic);

}
