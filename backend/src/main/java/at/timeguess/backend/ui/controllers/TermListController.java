package at.timeguess.backend.ui.controllers;

import java.io.FileReader;
import java.io.Serializable;
import java.util.List;

import javax.annotation.PostConstruct;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import at.timeguess.backend.model.Term;
import at.timeguess.backend.model.Topic;
import at.timeguess.backend.model.Exceptions.TermAlreadyExistsException;
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

    /**
     * Internal terms cache.
     */
    private List<Term> terms;

    @PostConstruct
    public void reloadTerms() {
        terms = termService.getAllTerms();
    }

    public List<Term> getTerms() {
        return terms;
    }

    public void doCreateTerm() {
        Term term = new Term();
        Topic topic = new Topic();
        term.setTopic(topic);
        try {
            term = termService.saveTerm(term);
            terms.add(term);
        } catch (TermAlreadyExistsException e) {
            ;
        };
    }

    public void doLoadTermsJSON(String filepath) throws Exception {
        Object jsonFile = new JSONParser().parse(new FileReader(filepath));
        JSONObject termsJSON = (JSONObject) jsonFile;

    }
    
}
