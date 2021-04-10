package at.timeguess.backend.model.api;

/**
 * A class that represents the response to messages sent by a Raspberry Pi
 * signaling general availability and containing information on battery
 * level and signal strength of the TimeFlip device.
 */
public class HealthResponse {

    private Boolean success; // indicates if message could be successfully processed

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

}
