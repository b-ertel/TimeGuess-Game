package at.timeguess.raspberry;

import java.util.logging.*;

import kong.unirest.Unirest;
import kong.unirest.UnirestException;
import kong.unirest.json.JSONException;

/**
 * A class that provides methods to send messages to the backend.
 */
public class MessageSender {

    private static final String FACETS_API_PATH = "/api/facets";
    private static final String STATUS_API_PATH = "/api/status";

    private static final String USERNAME = "cube";
    private static final String PASSWORD = "passwd123";

    private static final Logger LOGGER = Logger.getLogger("at.timeguess.raspberry");

    private String backendUrl;

    public MessageSender(String backendUrl) {
        this.backendUrl = backendUrl;
    }

    public String getBackendUrl() {
        return backendUrl;
    }

    public void setBackendUrl(String backendUrl) {
        this.backendUrl = backendUrl;
    }

    /**
     * Send a facets message to the backend.
     * 
     * @param facetsMessage the facets message
     */
    @SuppressWarnings("unchecked")
    public void sendFacetsMessage(FacetsMessage facetsMessage) {
        LOGGER.info("Sending facets message to backend ...");
        String body = String.format("{\"identifier\": \"%s\", \"facet\": %d}",
                facetsMessage.getIdentifier(),
                facetsMessage.getFacet());
        try {
            Unirest.post(backendUrl + FACETS_API_PATH)
                    .header("Content-Type", "application/json")
                    .body(body)
                    .basicAuth(USERNAME, PASSWORD)
                    .asEmpty()
                    .ifSuccess(response -> {
                        LOGGER.info("Facets message successfully processed by backend.");
                    })
                    .ifFailure(response -> {
                        LOGGER.severe("Facets message not successfully processed by backend!");
                    });
        }
        catch (UnirestException e) {
            LOGGER.warning("Could not connect to backend!");
        }
    }

    /**
     * Send a status message to the backend.
     * 
     * @param statusMessage the status message
     * @return the response if it has been received and could be successfully parsed or null otherwise
     */
    public StatusResponse sendStatusMessage(StatusMessage statusMessage) {
        LOGGER.info("Sending status message to backend ...");
        String body = String.format("{\"identifier\": \"%s\", \"calibrationVersion\": %d, \"batteryLevel\": %d, \"rssi\": %d}",
                statusMessage.getIdentifier(),
                statusMessage.getCalibrationVersion(),
                statusMessage.getBatteryLevel(),
                statusMessage.getRssi());
        DraftStatusResponse draftStatusResponse = new DraftStatusResponse();
        try {
            Unirest.post(backendUrl + STATUS_API_PATH)
                    .header("Content-Type", "application/json")
                    .body(body)
                    .basicAuth(USERNAME, PASSWORD)
                    .asJson()
                    .ifSuccess(response -> {
                        LOGGER.info("Status message successfully processed by backend.");
                        try {
                            LOGGER.info("Parsing status response ...");
                            int reportingInterval = response.getBody().getObject().getInt("reportingInterval");
                            int calibrationVersion = response.getBody().getObject().getInt("calibrationVersion");
                            draftStatusResponse.setReportingInterval(reportingInterval);
                            draftStatusResponse.setCalibrationVersion(calibrationVersion);
                        }
                        catch (JSONException e) {
                            LOGGER.severe("An error occurred while parsing the status response!");
                        }
                    })
                    .ifFailure(response -> {
                        LOGGER.severe("Status message not successfully processed by backend!");
                    });
        }
        catch (UnirestException e) {
            LOGGER.warning("Could not connect to backend!");
        }
        if (draftStatusResponse.getReportingInterval() != null && draftStatusResponse.getCalibrationVersion() != null) {
            StatusResponse statusResponse = new StatusResponse();
            statusResponse.setCalibrationVersion(draftStatusResponse.getCalibrationVersion());
            statusResponse.setReportingInterval(draftStatusResponse.getReportingInterval());
            return statusResponse;
        }
        else {
            return null;
        }
    }

    private static class DraftStatusResponse {

        private Integer reportingInterval;
        private Integer calibrationVersion;

        public Integer getReportingInterval() {
            return reportingInterval;
        }

        public void setReportingInterval(Integer reportingInterval) {
            this.reportingInterval = reportingInterval;
        }

        public Integer getCalibrationVersion() {
            return calibrationVersion;
        }

        public void setCalibrationVersion(Integer calibrationVersion) {
            this.calibrationVersion = calibrationVersion;
        }

    }

}
 