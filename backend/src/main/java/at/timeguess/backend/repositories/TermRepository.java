package at.timeguess.backend.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;

import org.springframework.data.repository.query.Param;
import at.timeguess.backend.model.Term;
import at.timeguess.backend.model.Topic;

public interface TermRepository extends AbstractRepository<Term, Long> {

    List<Term> findByTopic(Topic topic);

    Optional<Term> findById(Long id);

    @Query("SELECT COUNT (id) FROM Term")
    int nrOfTerms();

    @Query("SELECT COUNT(t.topic) FROM Term t WHERE t.topic = :topicid")
    int nrOfTermPerTopic(@Param("topicid") Topic topic);

    @Query("SELECT t FROM Term t WHERE t.name=:name AND t.topic=:topic")
    Term findByName(String name, Topic topic);
}
