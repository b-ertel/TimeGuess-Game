package at.timeguess.backend.model.exceptions;

/**
 * Exception to indicate that a topic already exists.
 */
public class AllTermsUsedInGameException extends Exception {

    private static final long serialVersionUID = 1L;

    public AllTermsUsedInGameException() {
        super("All terms have been used in previous rounds");
    }

    public AllTermsUsedInGameException(String message) {
        super(message);
    }

}
