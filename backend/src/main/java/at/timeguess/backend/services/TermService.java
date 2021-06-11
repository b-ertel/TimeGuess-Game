package at.timeguess.backend.services;

import java.util.List;
import java.util.Map;

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
import at.timeguess.backend.spring.CDIAwareBeanPostProcessor;
import at.timeguess.backend.ui.beans.MessageBean;
import at.timeguess.backend.ui.websockets.WebSocketManager;
import at.timeguess.backend.utils.CDIAutowired;
import at.timeguess.backend.utils.CDIContextRelated;

/**
 * Provides an interface to the model for managing {@link Term} entities.
 */
@Component
@Scope(WebApplicationContext.SCOPE_APPLICATION)
@CDIContextRelated
public class TermService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TermService.class);

    @Autowired
    private TermRepository termRepository;
    @Autowired
    private UserService userService;

    @Autowired
    private MessageBean messageBean;

    @CDIAutowired
    private WebSocketManager websocketManager;

    private Boolean infoOnSave = null;

    /**
     * @apiNote neither {@link Autowired} nor {@link CDIAutowired} work for a {@link Component},
     * and {@link PostConstruct} is not invoked, so autowiring is done manually
     */
    public TermService() {
        if (websocketManager == null) {
            new CDIAwareBeanPostProcessor().postProcessAfterInitialization(this, "websocketManager");
        }
    }

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
    public List<Term> getAllEnabledTermsOfTopic(Topic topic) {
        return termRepository.findEnablesByTopic(topic);
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
        return termRepository.findById(id).orElse(null);
    }

    /**
     * Setter to determine filling gui messages and triggering push updates on successful {@link saveTerm}.
     * If false subsequent calls to {@link saveTerm} will not trigger messages and pushs,
     * if true they will plus if since setting the value to false a successful save happened
     * a message and update will be triggered immediately.
     */
    public void setInfoOnSave(boolean value) {
        if (value) {
            if (infoOnSave != null && infoOnSave) {
                messageBean.alertInformation("Term update", "Multiple terms were successfully created");

                if (websocketManager != null)
                    websocketManager.getUserRegistrationChannel().send(
                        Map.of("type", "termUpdate", "name", "multiple", "id", 0L));
            }
            infoOnSave = null;
        }
        else
            infoOnSave = false;
    }

    /**
     * Saves the Term.
     * Additionally fills gui message with success or failure info and triggers a push update,
     * if {@link setInfoOnUpdate} is not set to false (by default true).
     * @param term the term to save
     * @return the saved term
     * @apiNote Message handling ist done here, because this is the central place for saving terms.
     */
    @PreAuthorize("hasAuthority('ADMIN') OR hasAuthority('MANAGER')")
    public Term saveTerm(Term term) {
        Term ret = null;
        try {
            boolean isNew = term.isNew();

            ret = termRepository.save(term);

            // fill ui message, send update and log
            if (infoOnSave == null) {
                messageBean.alertInformation(ret.getName(), isNew ? "New term created" : "Term updated");

                if (websocketManager != null)
                    websocketManager.getUserRegistrationChannel().send(
                        Map.of("type", "termUpdate", "name", term.getName(), "id", term.getId()));
            }
            else
                infoOnSave = true;

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
     * Additionally fills gui message with success or failure info and triggers a push update.
     * @param term the term to delete
     */
    @PreAuthorize("hasAuthority('ADMIN') OR hasAuthority('MANAGER')")
    public void deleteTerm(Term term) {
        try {
            termRepository.delete(term);

            // fill ui message, send update and log
            messageBean.alertInformation(term.getName(), "Term was deleted");

            if (websocketManager != null)
                websocketManager.getUserRegistrationChannel().send(
                    Map.of("type", "termUpdate", "name", term.getName(), "id", term.getId()));

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
