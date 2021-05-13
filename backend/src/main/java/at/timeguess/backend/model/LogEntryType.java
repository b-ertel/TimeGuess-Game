package at.timeguess.backend.model;

/**
 * A class which denotes a type of a logEntry.
 */
public enum LogEntryType {

    USER_LOGIN("joined the party"),
    USER_LOGOUT("has left the building"),
    USER_UNKNOWN("is not invited");

    private LogEntryType(String descriptionP) {
        this.description = descriptionP;
    }

    private String description;

    public String getDescription() {
        return description;
    }
}
