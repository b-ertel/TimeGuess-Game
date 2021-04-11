package at.timeguess.backend.model.api;

/**
 * A class that represents the messages sent by a Raspberry Pi
 * signaling general availability and containing information on battery
 * level and signal strength of the TimeFlip device.
 */
public class HealthMessage {

    private String identifier; // MAC address of the TimeFlip device
    private Integer batteryLevel; // current value of the Battery Level characteristic
    private Integer rssi; // current value of the RSSI property

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public Integer getBatteryLevel() {
        return batteryLevel;
    }

    public void setBatteryLevel(Integer batteryLevel) {
        this.batteryLevel = batteryLevel;
    }

    public Integer getRssi() {
        return rssi;
    }

    public void setRssi(Integer rssi) {
        this.rssi = rssi;
    }

}
