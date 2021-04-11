package at.timeguess.backend.ui.controllers;

import java.io.Serializable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import at.timeguess.backend.model.Term;
import at.timeguess.backend.model.Topic;
import at.timeguess.backend.model.exceptions.TermAlreadyExistsException;
import at.timeguess.backend.services.TermService;

/**
 * Controller for the term detail view.
 */
@Component
@Scope("view")
public class TermDetailController implements Serializable {

    private static final long serialVersionUID = 1L;

    @Autowired
    private TermService termService;

    /**
     * Attribute to cache the currently displayed Term
     */
    private Term term;

    /**
     * Sets the currently displayed term and reloads it form db. This term is
     * targeted by any further calls of
     * {@link #doReloadTerm()}, {@link #doSaveTerm()} and
     * {@link #doDeleteTerm()}.
     *
     * @param term
     */
    public void setTerm(Term term) {
        this.term = term;
        doReloadTerm();
    }

    /**
     * Returns the currently displayed term.
     *
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
        try {
            term = this.termService.saveTerm(term);
        } catch (TermAlreadyExistsException e) {
            ;   //TODO: show dialog that term already exists
        }
    }

    /**
     * Action to update the currently displayed term.
     */
    public void doUpdateTerm() {
        term = this.termService.updateTerm(term);
    }

    /**
     * Action to delete the currently displayed term.
     */
    public void doDeleteTerm() {
        this.termService.deleteTerm(term);
        term = null;
    }
    
}
