package at.timeguess.raspberry;

import java.util.logging.*;

import kong.unirest.Unirest;

import tinyb.BluetoothNotification;

/**
 * A BluetoothNotification sending facets messages to the backend
 * whenever a notification about a change of the facets characteristic is issued.
 */
public class FacetsMessageSender implements BluetoothNotification<byte[]> {

    private static final String API_PATH = "/api/facets";

    private final Logger logger = Logger.getLogger("at.timeguess.raspberry");

    private final String timeflipMacAddress;
    private final String backendUrl;
    private final CalibrationVersionHelper calibrationVersionHelper;

    public FacetsMessageSender(String timeflipMacAddress, String backendUrl, CalibrationVersionHelper calibrationVersionHelper) {
        this.timeflipMacAddress = timeflipMacAddress;
        this.backendUrl = backendUrl;
        this.calibrationVersionHelper = calibrationVersionHelper;
    }

    public void run(byte[] facetRaw) {
        logger.info("Sending facets message to backend ...");
        int oldCalibrationVersion = calibrationVersionHelper.readCalibrationVersion();
        String body = String.format("{\"identifier\": \"%s\", \"calibrationVersion\": %d, \"facet\": %d}",
                timeflipMacAddress,
                oldCalibrationVersion,
                facetRaw[0]);
        logger.info(body);
        Unirest.post(backendUrl + API_PATH)
                .header("Content-Type", "application/json")
                .body(body)
                .asJson()
                .ifSuccess(response -> {
                    logger.info("Facets message successfully processed by backend.");
                    int newCalibrationVersion = response.getBody().getObject().getInt("calibrationVersion");
                    if (newCalibrationVersion != oldCalibrationVersion) {
                        logger.info("Writing new value of " + newCalibrationVersion + " to the calibration version characteristic ...");
                        if (calibrationVersionHelper.writeCalibrationVersion(newCalibrationVersion)) {
                            logger.info("New value successfully written.");
                        }
                        else {
                            logger.warning("New value not successfully written!");
                        }
                    }
                })
                .ifFailure(response -> {
                    logger.warning("Facets message not successfully processed by backend!");
                });
    }

}
 