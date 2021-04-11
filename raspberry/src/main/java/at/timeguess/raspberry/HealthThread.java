package at.timeguess.raspberry;

import java.util.concurrent.TimeUnit;

import java.util.logging.Logger;

import kong.unirest.Unirest;

import tinyb.BluetoothDevice;
import tinyb.BluetoothGattCharacteristic;

/**
 * A thread for sending "health" information (i.e., battery level
 * and signal strength) to the backend.
 */
public class HealthThread extends Thread {

    private static final String API_PATH = "/api/health";

    private final Logger logger = Logger.getLogger("at.timeguess.raspberry");

    private final String timeflipMacAddress;
    private final String backendUrl;
    private final BluetoothDevice timeflip;
    private final BluetoothGattCharacteristic batteryLevelCharacteristic;
    private final Long healthReportingInterval;

    public HealthThread(String timeflipMacAddress, String backendUrl, BluetoothDevice timeflip, BluetoothGattCharacteristic batteryLevelCharacteristic, Long healthReportingInterval) {
        this.timeflipMacAddress = timeflipMacAddress;
        this.backendUrl = backendUrl;
        this.timeflip = timeflip;
        this.batteryLevelCharacteristic = batteryLevelCharacteristic;
        this.healthReportingInterval = healthReportingInterval;
    }

    @Override
    public void run() {
        while (true) {
            logger.info("Sending health request to backend ...");
            String body = String.format("{\"identifier\": \"%s\", \"batteryLevel\": %d, \"rssi\": %d}",
                    timeflipMacAddress,
                    batteryLevelCharacteristic.readValue()[0],
                    timeflip.getRSSI());
            Unirest.post(backendUrl + API_PATH)
                    .header("Content-Type", "application/json")
                    .body(body)
                    .asJson()
                    .ifSuccess(response -> {
                        logger.info("Health request successfully sent.");
                        boolean success = response.getBody().getObject().getBoolean("success");
                        if (success) {
                            logger.info("Health request successfully processed.");
                        }
                        else {
                            logger.warning("Health request not successfully processed.");
                        }
                    })
                    .ifFailure(response -> {
                        logger.warning("Health request not successfully sent.");
                    });
                    try {
                        TimeUnit.SECONDS.sleep(healthReportingInterval);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
        }
    }
}
