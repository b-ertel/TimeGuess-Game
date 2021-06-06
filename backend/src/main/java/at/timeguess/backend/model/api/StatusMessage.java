package at.timeguess.backend.model.api;

/**
 * A class for status messages.
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
