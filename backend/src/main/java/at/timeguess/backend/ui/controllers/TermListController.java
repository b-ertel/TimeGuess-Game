package at.timeguess.backend.ui.controllers;

import java.io.Serializable;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import at.timeguess.backend.model.Term;
import at.timeguess.backend.services.TermService;

/**
 * Controller for the term list view.
 */
@Component
@Scope("view")
public class TermListController implements Serializable {

    private static final long serialVersionUID = 1L;

    @Autowired
    private TermService termService;

    public List<Term> getTerms() {
        return termService.getAllTerms();
    }
    
}
