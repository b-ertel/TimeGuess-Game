package at.timeguess.backend.ui.beans;

import java.io.Serializable;

import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;

import at.timeguess.backend.model.Term;
import at.timeguess.backend.model.Topic;
import at.timeguess.backend.services.TermService;

/**
 * Bean for creating a new term.
 */
@Component
@Scope(WebApplicationContext.SCOPE_REQUEST)
public class NewTermBean implements Serializable {

    private static final long serialVersionUID = 1L;

    @Autowired
    private TermService termService;

    @Autowired
    private MessageBean messageBean;

    private String termname;

    private Topic topic;

    public String getTermName() {
        return termname;
    }

    public void setTermName(String name) {
        this.termname = name;
    }

    public Topic getTopic() {
        return topic;
    }

    public void setTopic(Topic topic) {
        this.topic = topic;
    }

    /**
     * Clears all fields.
     */
    public void clearFields() {
        this.setTermName(null);
        this.setTopic(null);
    }

    /**
     * Creates a new term with the settings saved.
     * @apiNote shows a ui message if input fields are invalid.
     */
    public Term createTerm() {
        if (this.validateInput()) {
            Term term = new Term();

            term.setName(termname);
            term.setTopic(topic);

            term = termService.saveTerm(term);
            this.clearFields();

            return term;
        }
        else {
            messageBean.alertErrorFailValidation("Term creation failed", "Input fields are invalid");
        }
        return null;
    }

    /**
     * Checks if all fields contain valid values.
     * @return
     */
    public boolean validateInput() {
        if (Strings.isBlank(termname)) return false;
        if (topic == null || topic.isNew()) return false;
        return true;
    }
}
