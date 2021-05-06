package at.timeguess.backend.model.api;

/**
 * A class that represents the messages from a Raspberry Pi
 * that has established a connection with a TimeFlip device
 * containing various status information.
 * <p>
 * Example:
 * <pre>
 * {
 *   "identifier": "56:23:89:34:56",
 *   "calibrationVersion": 0,
 *   "batteryLevel": 90,
 *   "rssi": -50
 * }
 * </pre>
 */
public class StatusMessage {
    
    private String identifier; // MAC address of the TimeFlip device
    private int calibrationVersion; // current value of the calibration version characteristic 
    private int batteryLevel; // current value of the Battery Level characteristic
    private int rssi; // current value of the RSSI property
    
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

    public int getBatteryLevel() {
        return batteryLevel;
    }

    public void setBatteryLevel(int batteryLevel) {
        this.batteryLevel = batteryLevel;
    }

    public int getRssi() {
        return rssi;
    }

    public void setRssi(int rssi) {
        this.rssi = rssi;
    }

}
