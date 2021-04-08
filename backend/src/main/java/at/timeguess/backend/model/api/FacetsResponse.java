package at.timeguess.backend.model.api;

/**
 * A class that represents the response sent to a TimeFlip device
 * to a message signaling a change of the facets characteristic.
 */
public class FacetsResponse {

    private Boolean success; // indicates if message could be successfully processed
    private Integer configuration; // new value to write to the Calibration version characteristic

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public Integer getConfiguration() {
        return configuration;
    }

    public void setConfiguration(Integer configuration) {
        this.configuration = configuration;
    }

}
