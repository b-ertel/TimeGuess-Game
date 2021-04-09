package at.timeguess.raspberry.notifications;

import java.util.logging.*;

import kong.unirest.Unirest;

import tinyb.BluetoothNotification;

/**
 * A BluetoothNotification for the battery level characteristic.
 */
public class BatteryLevelNotification implements BluetoothNotification<byte[]> {

    private static final String API_PATH = "/api/batterylevel";

    private final Logger logger = Logger.getLogger("at.timeguess.raspberry.notifications");

    private final String timeflipMacAddress;
    private final String backendUrl;

    public BatteryLevelNotification(String timeflipMacAddress, String backendUrl) {
        this.timeflipMacAddress = timeflipMacAddress;
        this.backendUrl = backendUrl;
    }

    /**
     * This method gets called whenever a notification about
     * a change of the battery level characteristic is issued.
     */
    public void run(byte[] batteryLevelRaw) {
        logger.info(String.format("Got notified that Battery level characteristic has new value 0x%02x.", batteryLevelRaw[0]));
        logger.info("Sending POST request to backend ...");
        String body = String.format("{\"identifier\": \"%s\", \"batteryLevel\": %d}",
                timeflipMacAddress,
                batteryLevelRaw[0]);
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
