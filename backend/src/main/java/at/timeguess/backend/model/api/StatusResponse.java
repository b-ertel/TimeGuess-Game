package at.timeguess.backend.model.api;

/**
 * A class that represents response to the regular status messages sent by a Raspberry Pi.
 */
public class StatusResponse {
    
    private int reportingInterval; // interval for sending the {@link StatusMessage}

    public int getReportingInterval() {
        return reportingInterval;
    }

    public void setReportingInterval(int reportingInterval) {
        this.reportingInterval = reportingInterval;
    }

}
