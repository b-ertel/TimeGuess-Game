package at.timeguess.backend.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import at.timeguess.backend.model.Term;
import at.timeguess.backend.model.Topic;

public interface TermRepository extends AbstractRepository<Term, Long> {
	
    List<Term> findByTopic(Topic topic);
    Term findById(Long id);
    Term findByName(String name);
    
    
    @Query("SELECT COUNT (id) FROM Term")
    int nrOfTerms();
    
    @Query("SELECT COUNT(t.topic) FROM Term t WHERE t.topic = :topicid")
    int nrOfTermPerTopic(@Param("topicid") Topic topic);
}
