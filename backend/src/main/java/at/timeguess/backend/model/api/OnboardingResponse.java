package at.timeguess.backend.model.api;

/**
 * A class that represents the response to messages sent by a Raspberry Pi
 * signaling successful startup and connection with a TimeFlip device.
 */
public class OnboardingResponse {

    private Boolean success; // indicates if message could be successfully processed

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

}
