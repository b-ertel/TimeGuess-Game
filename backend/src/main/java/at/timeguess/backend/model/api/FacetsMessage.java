package at.timeguess.backend.model.api;

/**
 * A class that represents the messages sent by a TimeFlip device
 * signaling a change of the facets characteristic.
 */
public class FacetsMessage {

    private String identifier; // MAC address of the TimeFlip device
    private int calibrationVersion; // current value of the Calibration version characteristic
    private int facet; // new value of the facets characteristic

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public int getCalibrationVersion() {
        return calibrationVersion;
    }

    public void setCalibrationVersion(int calibrationVersion) {
        this.calibrationVersion = calibrationVersion;
    }

    public int getFacet() {
        return facet;
    }

    public void setFacet(int facet) {
        this.facet = facet;
    }
    
}
