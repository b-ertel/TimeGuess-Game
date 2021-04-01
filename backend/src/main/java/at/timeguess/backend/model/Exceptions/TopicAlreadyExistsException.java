package at.timeguess.backend.model.Exceptions;

/**
 * Exception to indicate that a topic already exists.
 */
public class TopicAlreadyExistsException extends AlreadyExistsException {

    private static final long serialVersionUID = 1L;

    public TopicAlreadyExistsException() {
        super("Topic already exists!");
    }

    public TopicAlreadyExistsException(String message) {
        super(message);
    }

}
