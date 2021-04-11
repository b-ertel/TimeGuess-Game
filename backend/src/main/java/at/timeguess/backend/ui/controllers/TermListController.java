package at.timeguess.backend.ui.controllers;

import java.io.FileReader;
import java.io.Serializable;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Named;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.file.UploadedFile;
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
@Named
@RequestScoped
@Scope("view")
public class TermListController implements Serializable {

    private static final long serialVersionUID = 1L;

    @Autowired
    private TermService termService;

    /**
     * Internal terms cache.
     */
    private List<Term> terms;

    /**
     * Cache the uploaded file.
     */
    private UploadedFile file;

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

    public void doLoadTermsJSON(FileUploadEvent event) throws Exception {

        FacesMessage message = new FacesMessage("Successful", event.getFile().getFileName() + " is uploaded.");
        FacesContext.getCurrentInstance().addMessage(null, message);

        System.out.print(file);

        // Object jsonFile = new JSONParser().parse(file);
        // JSONObject termsJSON = (JSONObject) jsonFile;

    }
    
}
