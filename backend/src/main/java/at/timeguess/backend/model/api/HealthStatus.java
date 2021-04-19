package at.timeguess.backend.model.api;

import java.time.LocalDateTime;

public class HealthStatus {
	
    private int batteryLevel; 			// current value of the Battery Level characteristic
    private int rssi; 					// current value of the RSSI property
    private String mac;  				// identifier of the cube
    private LocalDateTime timestamp;
    
    
    public HealthStatus() {
    	
    }
    
    public HealthStatus(LocalDateTime time, int battery, int rssi, String mac) {
    	setBatteryLevel(battery);
    	setRssi(rssi);
    	setMac(mac);
    	setTimestamp(LocalDateTime.now());
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

	public LocalDateTime getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(LocalDateTime timestamp) {
		this.timestamp = timestamp;
	}

	public String getMac() {
		return mac;
	}

	public void setMac(String mac) {
		this.mac = mac;
	}
}
