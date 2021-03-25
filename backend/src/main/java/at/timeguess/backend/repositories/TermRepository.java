package at.timeguess.backend.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import at.timeguess.backend.model.Term;

public interface TermRepository extends AbstractRepository<Round, Long> {
	
    List<Term> findByTopic(Topic topic);
    Term findById(Long id);
    Term findByName(String name);

}
