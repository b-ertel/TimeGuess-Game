package at.timeguess.raspberry;

import java.util.concurrent.TimeUnit;

import java.util.logging.Logger;

import kong.unirest.Unirest;

import tinyb.BluetoothDevice;
import tinyb.BluetoothGattCharacteristic;

/**
 * A thread for sending status messages to the backend according to
 * the reporting interval defined by the backend.
 */
public class StatusMessageSender extends Thread {

    private static final String API_PATH = "/api/status";

    // initial reporting interval to be used until this gets set by the backend
    private static final int INITIAL_REPORTING_INTERVAL = 60;

    private final Logger logger = Logger.getLogger("at.timeguess.raspberry");

    private final String timeflipMacAddress;
    private final String backendUrl;
    private final CalibrationVersionHelper calibrationVersionHelper;
    private final BluetoothGattCharacteristic batteryLevelCharacteristic;
    private final BluetoothDevice timeflip;

    private int reportingInterval = INITIAL_REPORTING_INTERVAL;

    public StatusMessageSender(String timeflipMacAddress, String backendUrl, CalibrationVersionHelper calibrationVersionHelper, BluetoothGattCharacteristic batteryLevelCharacteristic, BluetoothDevice timeflip) {
        this.timeflipMacAddress = timeflipMacAddress;
        this.backendUrl = backendUrl;
        this.calibrationVersionHelper = calibrationVersionHelper;
        this.batteryLevelCharacteristic = batteryLevelCharacteristic;
        this.timeflip = timeflip;
    }

    @Override
    public void run() {
        while (true) {
            logger.info("Sending status message to backend ...");
            String body = String.format("{\"identifier\": \"%s\", \"calibrationVersion\": %d, \"batteryLevel\": %d, \"rssi\": %d}",
                    timeflipMacAddress,
                    calibrationVersionHelper.readCalibrationVersion(),
                    batteryLevelCharacteristic.readValue()[0],
                    timeflip.getRSSI());
            logger.info(body);
            Unirest.post(backendUrl + API_PATH)
                    .header("Content-Type", "application/json")
                    .body(body)
                    .asJson()
                    .ifSuccess(response -> {
                        logger.info("Status message successfully processed by backend.");
                        int newReportingInterval = response.getBody().getObject().getInt("reportingInterval");
                        if (newReportingInterval > 0) {
                            logger.info("Setting reporting interval to " + newReportingInterval + " ...");
                            reportingInterval = newReportingInterval;
                        }
                    })
                    .ifFailure(response -> {
                        logger.warning("Status message not successfully processed by backend!");
                    });
            try {
                TimeUnit.SECONDS.sleep(reportingInterval);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
}