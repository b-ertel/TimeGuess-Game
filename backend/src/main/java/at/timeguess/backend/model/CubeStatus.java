package at.timeguess.backend.model;

/**
 * Enumeration of different states a TimeFlip device can be in.
 */
public enum CubeStatus {

    LIVE(" and has to be configured"),
    READY(" to play"),
    IN_CONFIG(""),
    IN_GAME(""),
    OFFLINE("");

    private CubeStatus(String message) {
        this.message = message;
    }

    private String message;

    public String getMessage() {
        return this.message;
    }
}
