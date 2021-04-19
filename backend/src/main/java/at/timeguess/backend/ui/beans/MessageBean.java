package at.timeguess.backend.ui.beans;

import javax.annotation.ManagedBean;
import javax.faces.application.FacesMessage;
import javax.faces.application.FacesMessage.Severity;
import javax.faces.context.FacesContext;
import java.io.Serializable;

/**
 * Bean to show messages in the faces UI.
 */
@ManagedBean
public class MessageBean implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Show information message
     * @param header Header to be displayed.
     * @param text   Text to be displayed.
     */
    public void alertInformation(String header, String text) {
        alert(FacesMessage.SEVERITY_INFO, header, text);
    }

    /**
     * Show warning message
     * @param header Header to be displayed.
     * @param text   Text to be displayed.
     */
    public void alertWarning(String header, String text) {
        alert(FacesMessage.SEVERITY_WARN, header, text);
    }

    /**
     * Show error message
     * @param header Header to be displayed.
     * @param text   Text to be displayed.
     */
    public void alertError(String header, String text) {
        alertError(header, text, false);
    }

    public void alertErrorFailValidation(String header, String text) {
        alertError(header, text, true);
    }

    private void alertError(String header, String text, boolean failValidation) {
        FacesContext context = FacesContext.getCurrentInstance();
        if (context != null) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, header, text));

            if (failValidation) context.validationFailed();
        }
    }

    private void alert(Severity severity, String header, String text) {
        FacesContext context = FacesContext.getCurrentInstance();
        if (context != null) context.addMessage(null, new FacesMessage(severity, header, text));
    }
}
