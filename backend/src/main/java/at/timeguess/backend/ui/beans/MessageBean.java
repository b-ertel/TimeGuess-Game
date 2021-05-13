package at.timeguess.backend.ui.beans;

import javax.annotation.ManagedBean;
import javax.faces.application.FacesMessage;
import javax.faces.application.FacesMessage.Severity;
import javax.faces.context.FacesContext;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;

/**
 * Bean to show messages in the faces UI.
 */
@ManagedBean
public class MessageBean implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Show information message
     * @param header header to be displayed
     * @param text   text to be displayed
     */
    public void alertInformation(String header, String text) {
        alert(FacesMessage.SEVERITY_INFO, header, text);
    }

    /**
     * Shows warning message.
     * @param header header to be displayed
     * @param text   text to be displayed
     */
    public void alertWarning(String header, String text) {
        alert(FacesMessage.SEVERITY_WARN, header, text);
    }

    /**
     * Shows error message.
     * @param header header to be displayed
     * @param text   text to be displayed
     */
    public void alertError(String header, String text) {
        alertError(header, text, false);
    }
    
    /**
     * Shows multiple error message.
     * @param header header to be displayed
     * @param messages   messages to be displayed
     */
    public void alertError(String header, List<String> messages) {
        alertError(header, messages, false);
    }

    /**
     * Shows error message and marks context with validation error.
     * @param header header to be displayed
     * @param text   text to be displayed
     */
    public void alertErrorFailValidation(String header, String text) {
        alertError(header, text, true);
    }

    /**
     * Redirects user to given page.
     * @param toPage
     */
    public void redirect(String toPage) {
        FacesContext context = FacesContext.getCurrentInstance();
        if (context != null) try {
            context.getExternalContext().redirect(toPage);
        }
        catch (IOException e) {
            alertError(toPage, "Redirection failed");
            e.printStackTrace();
        }
    }

    private void alertError(String header, String text, boolean failValidation) {
        FacesContext context = FacesContext.getCurrentInstance();
        if (context != null) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, header, text));

            if (failValidation) context.validationFailed();
        }
    }
    
    private void alertError(String header, List<String> messages, boolean failValidation) {
        FacesContext context = FacesContext.getCurrentInstance();
        if (context != null) {
        	for(String s : messages) {
        		context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, header, s));
        		if (failValidation) context.validationFailed();
        	}
        }
    }

    private void alert(Severity severity, String header, String text) {
        FacesContext context = FacesContext.getCurrentInstance();
        if (context != null) context.addMessage(null, new FacesMessage(severity, header, text));
    }
}
