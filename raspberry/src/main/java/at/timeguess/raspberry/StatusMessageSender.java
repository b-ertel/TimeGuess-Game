package at.timeguess.raspberry;

import java.util.concurrent.TimeUnit;

import java.util.logging.Logger;

import kong.unirest.Unirest;
import kong.unirest.UnirestException;

import tinyb.BluetoothDevice;
import tinyb.BluetoothException;
import tinyb.BluetoothGattCharacteristic;

/**
 * A thread for sending status messages to the backend according to
 * the reporting interval defined by the backend.
 */
public class StatusMessageSender extends Thread {

    private static final String API_PATH = "/api/status";

    // initial reporting interval to be used until this gets set by the backend
    private static final int INITIAL_REPORTING_INTERVAL = 10;

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
        logger.info("Status messages enabled.");
        while (!isInterrupted()) {
            try {
                logger.info("Retrieving values ...");
                int oldCalibrationVersion = calibrationVersionHelper.readCalibrationVersion();
                byte[] batteryLevelRaw = batteryLevelCharacteristic.readValue();
                short rssi = timeflip.getRSSI();
                logger.info("Sending status message to backend ...");
                String body = String.format("{\"identifier\": \"%s\", \"calibrationVersion\": %d, \"batteryLevel\": %d, \"rssi\": %d}",
                        timeflipMacAddress,
                        oldCalibrationVersion,
                        batteryLevelRaw[0],
                        rssi);
                Unirest.post(backendUrl + API_PATH)
                        .header("Content-Type", "application/json")
                        .body(body)
                        .basicAuth("cube", "passwd")
                        .asJson()
                        .ifSuccess(response -> {
                            logger.info("Status message successfully processed by backend.");
                            int newReportingInterval = response.getBody().getObject().getInt("reportingInterval");
                            logger.info("Reporting interval from response is " + newReportingInterval + ".");
                            if (newReportingInterval > 0) {
                                logger.info("Setting reporting interval to " + newReportingInterval + " ...");
                                reportingInterval = newReportingInterval;
                            }
                            else {
                                logger.warning("Recieved value for reporting value is ignored since it is not a positive integer!");
                            }
                            int newCalibrationVersion = response.getBody().getObject().getInt("calibrationVersion");
                            logger.info("Calibration version from response is " + newCalibrationVersion + ".");
                            if (newCalibrationVersion != oldCalibrationVersion) {
                                logger.info("Writing new value of " + newCalibrationVersion + " to the calibration version characteristic ...");
                                if (calibrationVersionHelper.writeCalibrationVersion(newCalibrationVersion)) {
                                    logger.info("New value successfully written.");
                                }
                                else {
                                    logger.warning("New value not successfully written!");
                                }
                            }
                            else {
                                logger.info("No action required.");
                            }
                        })
                        .ifFailure(response -> {
                            logger.warning("Status message not successfully processed by backend!");
                        });
            }
            catch (UnirestException e) {
                logger.warning("Could not connect to backend!");
            }
            catch (BluetoothException e) {
                logger.warning("A problem with the Bluetooth connection occurred!");
            }
            try {
                TimeUnit.SECONDS.sleep(reportingInterval);
            } catch (InterruptedException e) {
                interrupt();
            }
        }
        logger.warning("Status messages disabled!");
    }
}
