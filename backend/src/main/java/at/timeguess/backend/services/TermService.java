package at.timeguess.backend.services;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;

import at.timeguess.backend.model.Term;
import at.timeguess.backend.model.Topic;
import at.timeguess.backend.model.User;
import at.timeguess.backend.repositories.TermRepository;
import at.timeguess.backend.ui.beans.MessageBean;

/**
 * Provides an interface to the model for managing {@link Term} entities.
 */
@Component
@Scope(WebApplicationContext.SCOPE_APPLICATION)
public class TermService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TermService.class);

    @Autowired
    private TermRepository termRepository;
    @Autowired
    private UserService userService;

    @Autowired
    private MessageBean messageBean;

    /**
     * Returns a list of all terms.
     * @return list of all terms
     */
    @PreAuthorize("hasAuthority('PLAYER') or hasAuthority('MANAGER')")
    public List<Term> getAllTerms() {
        return termRepository.findAll();
    }

    /**
     * Returns a list of all terms from a single topic.
     * @param topic the topic whose terms are returned
     * @return list of terms
     */
    public List<Term> getAllTermsOfTopic(Topic topic) {
        return termRepository.findByTopic(topic);
    }

    /**
     * Loads a single Term identified by it's ID.
     * @param id the id of the term to load
     * @return a single Term
     */
    @PreAuthorize("hasAuthority('ADMIN') OR hasAuthority('MANAGER')")
    public Term loadTerm(Long id) {
        return termRepository.findById(id).get();
    }

    /**
     * Saves the Term.
     * @param term the term to save
     * @return the new term
     */
    @PreAuthorize("hasAuthority('ADMIN') OR hasAuthority('MANAGER')")
    public Term saveTerm(Term term) {
        Term ret = null;
        try {
            boolean isNew = term.isNew();

            ret = termRepository.save(term);

            // show ui message and log
            messageBean.alertInformation(ret.getName(), isNew ? "New term created" : "Term updated");

            LOGGER.info("Term '{}' (id={}) was {}", ret.getName(), ret.getId(), isNew ? "created" : "updated");
        }
        catch (Exception e) {
            String msg = "Saving term failed";
            if (e.getMessage().contains("TERM(TOPIC, NAME)"))
                msg += String.format(": term named '%s' already exists", term.getName());
            messageBean.alertErrorFailValidation(term.getName(), msg);

            LOGGER.info("Saving term '{}' (id={}) failed, stack trace:", term.getName(), term.getId());
            e.printStackTrace();
        }
        return ret;
    }

    /**
     * Deletes the term.
     * @param term the term to delete
     */
    @PreAuthorize("hasAuthority('ADMIN') OR hasAuthority('MANAGER')")
    public void deleteTerm(Term term) {
        try {
            termRepository.delete(term);

            // show ui message and log
            messageBean.alertInformation(term.getName(), "Term was deleted");

            User auth = userService.getAuthenticatedUser();
            LOGGER.info("Term '{}' (id={}) was deleted by User '{}' (id={})", term.getName(), term.getId(),
                    auth.getUsername(), auth.getId());
        }
        catch (Exception e) {
            String name = term == null ? "Unknown" : term.getName();
            messageBean.alertErrorFailValidation(name, "Deleting term failed");
            LOGGER.info("Deleting term '{}' (id={}) failed, stack trace:", name, term == null ? "null" : term.getId());
            e.printStackTrace();
        }
    }
}
