package at.timeguess.backend.model.exceptions;

/**
 * Exception to indicate that a term already exists.
 */
public class TermAlreadyExistsException extends AlreadyExistsException {

    private static final long serialVersionUID = 1L;

    public TermAlreadyExistsException() {
        super("Term already exists!");
    }

    public TermAlreadyExistsException(String message) {
        super(message);
    }

}
