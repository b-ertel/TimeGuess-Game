package at.timeguess.backend.model.api;

/**
 * A class that represents the response sent to a TimeFlip device
 * to a message signaling a change of the facets characteristic.
 */
public class FacetsResponse {

    private int calibrationVersion; // new value to write to the Calibration version characteristic

    public int getCalibrationVersion() {
        return calibrationVersion;
    }

    public void setCalibrationVersion(int calibrationVersion) {
        this.calibrationVersion = calibrationVersion;
    }

}
