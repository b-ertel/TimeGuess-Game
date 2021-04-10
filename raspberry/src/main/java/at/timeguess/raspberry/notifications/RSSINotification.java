package at.timeguess.raspberry.notifications;

import java.util.logging.*;

import kong.unirest.Unirest;

import tinyb.BluetoothNotification;

/**
 * A BluetoothNotification for the RSSI property of the TimeFlip device.
 */
public class RSSINotification implements BluetoothNotification<Short> {

    private static final String API_PATH = "/api/rssi";

    private final Logger logger = Logger.getLogger("at.timeguess.raspberry.notifications");

    private final String timeflipMacAddress;
    private final String backendUrl;

    public RSSINotification(String timeflipMacAddress, String backendUrl) {
        this.timeflipMacAddress = timeflipMacAddress;
        this.backendUrl = backendUrl;
    }

    /**
     * This method gets called whenever a notification about
     * a change of the RSSI property is issued.
     */
    public void run(Short RSSI) {
        logger.info("Got notified that TimeFlip device's RSSI has changed to " + RSSI + ".");
        logger.info("Sending POST request to backend ...");
        String body = String.format("{\"identifier\": \"%s\", \"rssi\": %d}",
                timeflipMacAddress,
                RSSI);
        Unirest.post(backendUrl + API_PATH)
                .header("Content-Type", "application/json")
                .body(body)
                .asJson()
                .ifSuccess(response -> {
                    logger.info("POST request accepted.");
                    boolean success = response.getBody().getObject().getBoolean("success");
                    logger.info("Backend says that POST request was processed " + (success ? "successfully" : "unsuccessfully") + ".");
                })
                .ifFailure(response -> {
                    logger.warning("POST request rejected.");
                });
    }

}
