package at.timeguess.backend.model.api;

/**
 * A class that represents the response sent to a TimeFlip device
 * to a message signaling a change of the RSSI property.
 */
public class RSSIResponse {

    private Boolean success; // indicates if message could be successfully processed

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

}
