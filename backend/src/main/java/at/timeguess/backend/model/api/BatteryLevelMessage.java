package at.timeguess.backend.model.api;

/**
 * A class that represents the messages sent by a TimeFlip device
 * signaling a change of the Battery level characteristic.
 */
public class BatteryLevelMessage {

    private String identifier; // MAC address of the TimeFlip device
    private Integer batteryLevel; // new value of the Battery Level characteristic

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

}
