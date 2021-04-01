package at.timeguess.backend.model.Exceptions;

/**
 * Base class for exceptions for already existing Topics/Terms.
 */
public abstract class AlreadyExistsException extends Exception {

    private static final long serialVersionUID = 1L;

    public AlreadyExistsException(String message) {
        super(message);
    }

}
