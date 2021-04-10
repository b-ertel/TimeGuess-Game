package at.timeguess.backend.model.api;

/**
 * A class that represents the messages sent by a TimeFlip device
 * signaling a change of the connected property.
 */
public class ConnectedMessage {

    private String identifier; // MAC address of the TimeFlip device
    private Boolean connected; // new value of the connected property
    private Integer calibration;

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public Boolean getConnected() {
        return connected;
    }

    public void setConnected(Boolean connected) {
        this.connected = connected;
    }

	public Integer getCalibration() {
		return calibration;
	}

	public void setCalibration(Integer calibration) {
		this.calibration = calibration;
	}

}
