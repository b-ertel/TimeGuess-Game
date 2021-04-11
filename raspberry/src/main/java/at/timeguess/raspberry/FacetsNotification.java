package at.timeguess.raspberry;

import java.util.logging.*;

import kong.unirest.Unirest;

import tinyb.BluetoothNotification;

/**
 * A BluetoothNotification for the facets characteristic.
 */
public class FacetsNotification implements BluetoothNotification<byte[]> {

    private static final String API_PATH = "/api/facets";

    private final Logger logger = Logger.getLogger("at.timeguess.raspberry");

    private final String timeflipMacAddress;
    private final String backendUrl;
    private final CalibrationVersionHelper calibrationVersionHelper;

    public FacetsNotification(String timeflipMacAddress, String backendUrl, CalibrationVersionHelper calibrationVersionHelper) {
        this.timeflipMacAddress = timeflipMacAddress;
        this.backendUrl = backendUrl;
        this.calibrationVersionHelper = calibrationVersionHelper;
    }

    /**
     * This method gets called whenever a notification about
     * a change of the facets characteristic is issued.
     */
    public void run(byte[] facetRaw) {
        logger.info(String.format("Got notified that Facets characteristic has new value 0x%02x.", facetRaw[0]));
        logger.info("Reading calibration version ...");
        int oldCalibrationVersion = calibrationVersionHelper.readCalibrationVersion();
        logger.info("Calibration version is currently " + oldCalibrationVersion + ".");
        logger.info("Sending POST request to backend ...");
        String body = String.format("{\"identifier\": \"%s\", \"configuration\": %d, \"facet\": %d}",
                timeflipMacAddress,
                oldCalibrationVersion,
                facetRaw[0]);
        Unirest.post(backendUrl + API_PATH)
                .header("Content-Type", "application/json")
                .body(body)
                .asJson()
                .ifSuccess(response -> {
                    logger.info("POST request accepted.");
                    boolean success = response.getBody().getObject().getBoolean("success");
                    logger.info("Backend says that POST request was processed " + (success ? "successfully" : "unsuccessfully") + ".");
                    int newCalibrationVersion = response.getBody().getObject().getInt("configuration");
                    logger.info("Backend says that calibration version should be " + newCalibrationVersion + ".");
                    if (newCalibrationVersion != oldCalibrationVersion) {
                        logger.info("Writing new calibration version to TimeFlip device ...");
                        if (calibrationVersionHelper.writeCalibrationVersion(newCalibrationVersion)) {
                            logger.info("New calibration version written successfully.");
                        }
                        else {
                            logger.warning("New calibration version could not be written to device.");
                        }
                    }
                    else {
                        logger.info("No further action required.");
                    }
                })
                .ifFailure(response -> {
                    logger.warning("POST request rejected.");
                });
    }

}
