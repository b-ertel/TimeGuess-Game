package at.timeguess.backend.services;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;

import at.timeguess.backend.model.Term;
import at.timeguess.backend.model.Topic;
import at.timeguess.backend.model.exceptions.TermAlreadyExistsException;
import at.timeguess.backend.repositories.TermRepository;
import at.timeguess.backend.repositories.TopicRepository;

/**
 * Provides an interface to the model for managing {@link Term} entities.
 */
@Component
@Scope("application")
public class TermService {

    @Autowired
    private TermRepository termRepository;

    @Autowired
    private TopicRepository topicRepository;

    /**
     * Returns a list of all terms.
     *
     * @return list of all terms
     */
    @PreAuthorize("hasAuthority('PLAYER') or hasAuthority('MANAGER')")
    public List<Term> getAllTerms() {
        return termRepository.findAll();
    }

    /**
     * Returns a list of all terms from a single topic.
     *
     * @param topic the topic whose terms are returned
     * @return list of terms
     */
    public List<Term> getAllTermsOfTopic(Topic topic) {
        return termRepository.findByTopic(topic);
    }

    /**
     * Loads a single Term identified by it's ID.
     *
     * @param id the id of the term to load
     * @return a single Term
     */
    @PreAuthorize("hasAuthority('MANAGER')")
    public Term loadTerm(Long id) {
        return termRepository.findById(id).get();
    }

    /**
     * Loads a single Term identified by it's name.
     *
     * @param name the name of the term to load
     * @return a single Term
     */
    @PreAuthorize("hasAuthority('MANAGER')")
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
    @PreAuthorize("hasAuthority('MANAGER')")
    public Term saveTerm(Term term) throws TermAlreadyExistsException {
        if (term.isNew()) {
            Term newTerm = termRepository.save(term);
            return newTerm;
        } else {
            throw new TermAlreadyExistsException();
        }
    }

    /**
     * Updates the Term. 
     *
     * @param term the term to update
     * @return the updated term
     */
    @PreAuthorize("hasAuthority('MANAGER')")
    public Term updateTerm(Term term) {
        String topicName = term.getTopic().getName();
        List<Topic> topicList = topicRepository.findAll();
        List<String> topicNames = new ArrayList<>();
        for (Topic t : topicList) {
            topicNames.add(t.getName());
        }
        if (topicNames.contains(topicName)) {
            Topic newTopic = topicRepository.findByName(topicName);
            term.setTopic(newTopic);
        } else {
            Topic newTopic = new Topic();
            newTopic.setName(topicName);
            topicRepository.save(newTopic);
            term.setTopic(newTopic);
        }
        Term newTerm = termRepository.save(term);
        return newTerm;
    }

    /**
     * Deletes the term.
     *
     * @param term the term to delete
     */
    @PreAuthorize("hasAuthority('MANAGER')")
    public void deleteTerm(Term term) {
        termRepository.delete(term);
    }

}
