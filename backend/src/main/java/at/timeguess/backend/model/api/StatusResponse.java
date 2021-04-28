package at.timeguess.backend.model.api;

/**
 * A class that represents the response to a {@link StatusMessage}.
 */
public class StatusResponse {
    
    private int reportingInterval; // interval for sending status messages
    private int calibrationVersion; // new value to write to the Calibration version characteristic

    public int getReportingInterval() {
        return reportingInterval;
    }

    public void setReportingInterval(int reportingInterval) {
        this.reportingInterval = reportingInterval;
    }

    public int getCalibrationVersion() {
        return calibrationVersion;
    }

    public void setCalibrationVersion(int calibrationVersion) {
        this.calibrationVersion = calibrationVersion;
    }

}
