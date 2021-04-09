package at.timeguess.backend.model.api;

/**
 * A class that represents the messages sent by a Raspberry Pi
 * signaling successful startup and connection with a TimeFlip device.
 */
public class HelloMessage {

    private String identifier; // MAC address of the TimeFlip device
    private Integer calibrationVersion; // current value of the calibration version characteristic 

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public Integer getCalibrationVersion() {
        return calibrationVersion;
    }

    public void setCalibrationVersion(Integer calibrationVersion) {
        this.calibrationVersion = calibrationVersion;
    }

}
