package at.timeguess.backend.model;

public enum CubeStatus {
	LIVE("has to be configured"), 
	READY("read to play");
	
	private CubeStatus(String message) {
		
	}
	
}
