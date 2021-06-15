package at.timeguess.backend.ui.controllers;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;

import at.timeguess.backend.model.Term;
import at.timeguess.backend.services.TermService;

/**
 * Controller for the term list view.
 */
@Component
@Scope(WebApplicationContext.SCOPE_SESSION)
public class TermListController implements Serializable {

    private static final long serialVersionUID = 1L;

    @Autowired
    private TermService termService;

    /**
     * Internal terms cache.
     */
    private Collection<Term> filterTerms;
    private Term selectedTerm;

    /**
     * Returns a list of all all terms.
     * @return list of terms
     */
    public List<Term> getTerms() {
        return termService.getAllTerms();
    }

    /**
     * Returns and sets a list of games, by default all returned by {@link #getTerms()}
     * (helper methods for primefaces datatable filter and sort).
     * @return collection of terms
     */
    public Collection<Term> getFilterTerms() {
        if (filterTerms == null) filterTerms = getTerms();
        return filterTerms;
    }

    public void setFilterTerms(Collection<Term> terms) {
        filterTerms = terms;
    }

    /**
     * Returns and sets the currently selected game (helper methods for primefaces datatable contextmenu).
     * @return term
     */
    public Term getSelectedTerm() {
        return selectedTerm;
    }

    public void setSelectedTerm(Term selectedTerm) {
        this.selectedTerm = selectedTerm;
    }
}
