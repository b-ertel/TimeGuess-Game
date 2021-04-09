package at.timeguess.backend.model.api;

/**
 * A class that represents the messages sent by a TimeFlip device
 * signaling a change of the RSSI property.
 */
public class RSSIMessage {

    private String identifier; // MAC address of the TimeFlip device
    private Integer RSSI; // new value of the RSSI property

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public Integer getRSSI() {
        return RSSI;
    }

    public void setRSSI(Integer RSSI) {
        this.RSSI = RSSI;
    }

}
