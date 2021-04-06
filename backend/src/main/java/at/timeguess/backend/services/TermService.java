package at.timeguess.backend.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;

import at.timeguess.backend.model.Term;
import at.timeguess.backend.model.Topic;
import at.timeguess.backend.model.exceptions.TermAlreadyExistsException;
import at.timeguess.backend.repositories.TermRepository;

/**
 * Provides an interface to the model for managing {@link Term} entities.
 */
@Component
@Scope("application")
public class TermService {

    @Autowired
    private TermRepository termRepository;

    /**
     * Returns a list of all terms from a single topic.
     *
     * @param topic the topic whose terms are returned
     * @return list of terms
     */
    @PreAuthorize("hasAuthority('ADMIN')")
    public List<Term> getAllTopics(Topic topic) {
        return termRepository.findByTopic(topic);
    }

    /**
     * Loads a single Term identified by it's ID.
     *
     * @param id the id of the term to load
     * @return a single Term
     */
    @PreAuthorize("hasAuthority('ADMIN')")
    public Term loadTerm(Long id) {
        return termRepository.findById(id);
    }

    /**
     * Loads a single Term identified by it's name.
     *
     * @param name the name of the term to load
     * @return a single Term
     */
    @PreAuthorize("hasAuthority('ADMIN')")
    public Term loadTerm(String name, Topic topic) {
        return termRepository.findByName(name, topic);
    }

    /**
     * Saves the Term.
     *
     * @param term the term to save
     * @return the new term
     * @throws TermAlreadyExistsException
     */
    @PreAuthorize("hasAuthority('ADMIN')")
    public Term saveTerm(Term term) throws TermAlreadyExistsException {
        if (term.isNew()) {
            Term newTerm = termRepository.save(term);
            return newTerm;
        } else {
            throw new TermAlreadyExistsException();
        }
    }
}
