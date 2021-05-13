package at.timeguess.backend.ui.controllers;

import java.io.Serializable;

import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;

import at.timeguess.backend.model.Term;
import at.timeguess.backend.services.TermService;
import at.timeguess.backend.ui.beans.MessageBean;

/**
 * Controller for the term detail view.
 */
@Component
@Scope(WebApplicationContext.SCOPE_SESSION)
public class TermDetailController implements Serializable {

    private static final long serialVersionUID = 1L;

    @Autowired
    private TermService termService;

    @Autowired
    private MessageBean messageBean;

    /**
     * Attribute to cache the currently displayed Term
     */
    private Term term;

    /**
     * Sets the currently displayed term and reloads it form db.
     * This term is targeted by any further calls of {@link #doReloadTerm()}, {@link #doSaveTerm()} and {@link #doDeleteTerm()}.
     * @param term
     */
    public void setTerm(Term term) {
        this.term = term;
        doReloadTerm();
    }

    /**
     * Returns the currently displayed term.
     * @return the currently displayed term
     */
    public Term getTerm() {
        return term;
    }

    /**
     * Action to force a reload of the currently displayed term.
     */
    public void doReloadTerm() {
        term = termService.loadTerm(term.getId());
    }

    /**
     * Action to save the currently displayed term.
     */
    public void doSaveTerm() {
        if (doValidateTerm()) {
            Term ret = this.termService.saveTerm(term);
            if (ret != null) term = ret;
        }
        else
            messageBean.alertErrorFailValidation("Saving term failed", "Input fields are invalid");
    }

    public void doSaveTerm(Term selectedTerm) {
        this.termService.saveTerm(selectedTerm);
        if (selectedTerm.isEnabled()) {
            messageBean.alertInformation("Successfully enabled", String.format("Term %s enabled.", selectedTerm.getName()));
        } else {
            messageBean.alertInformation("Successfully disabled", String.format("Term %s disabled.", selectedTerm.getName()));
        }
    }

    /**
     * Action to delete the currently displayed term.
     */
    public void doDeleteTerm() {
        this.termService.deleteTerm(term);
        term = null;
    }

    /**
     * Checks if all fields contain valid values.
     * @return true if all fields contain valid values, false otherwise
     */
    public boolean doValidateTerm() {
        if (Strings.isBlank(term.getName())) return false;
        if (term.getTopic() == null || term.getTopic().isNew()) return false;
        return true;
    }
}
