package at.timeguess.backend.ui.beans;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.ManagedBean;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import java.io.Serializable;

/**
 * Bean to show messages in the faces UI.
 */
@ManagedBean
public class MessageBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    /**
     * Show success message
     * @param summary Short summary.
     * @param text    Text to be displayed.
     */
    public void alertInformation(String summary, String text) {
        LOGGER.info("Message Success: " + text);
        FacesContext context = FacesContext.getCurrentInstance();
        if (context != null)
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, summary, text));
    }

    /**
     * Show error message
     * @param summary Short summary.
     * @param text    Text to be displayed.
     */
    public void alertError(String summary, String text) {
        alertError(summary, text, false);
    }

    public void alertErrorFailValidation(String summary, String text) {
        alertError(summary, text, true);
    }

    private void alertError(String summary, String text, boolean failValidation) {
        LOGGER.info("Message Error: " + text);
        FacesContext context = FacesContext.getCurrentInstance();
        if (context != null) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, summary, text));

            if (failValidation)
                context.validationFailed();
        }
    }
}
