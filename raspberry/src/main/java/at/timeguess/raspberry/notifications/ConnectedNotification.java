package at.timeguess.raspberry.notifications;

import java.util.logging.*;

import kong.unirest.Unirest;

import tinyb.BluetoothNotification;

/**
 * A BluetoothNotification for the connected property of the TimeFlip device.
 */
public class ConnectedNotification implements BluetoothNotification<Boolean> {

    private static final String API_PATH = "/api/connected";

    private final Logger logger = Logger.getLogger("at.timeguess.raspberry.notifications");

    private final String timeflipMacAddress;
    private final String backendUrl;

    public ConnectedNotification(String timeflipMacAddress, String backendUrl) {
        this.timeflipMacAddress = timeflipMacAddress;
        this.backendUrl = backendUrl;
    }

    /**
     * This method gets called whenever a notification about
     * a change of the connected property is issued.
     */
    public void run(Boolean connected) {
        logger.info("Got notified that TimeFlip device " + (connected ? "connected" : "disconnected") + ".");
        logger.info("Sending POST request to backend ...");
        String body = String.format("{\"identifier\": \"%s\", \"connected\": %b}",
                timeflipMacAddress,
                connected);
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
