package at.timeguess.raspberry;

import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import tinyb.BluetoothException;

/**
 * A thread that regularly sends status messages to the backend and
 * processes the status responses it receives.
 */
public class StatusThread extends Thread {

    private static final int INITIAL_REPORTING_INTERVAL = 10;

    private static final Logger LOGGER = Logger.getLogger("at.timeguess.raspberry");

    private final TimeFlipWrapper timeFlipWrapper;
    private final MessageSender messageSender;

    public StatusThread(final TimeFlipWrapper timeFlipWrapper, final MessageSender messageSender) {
        this.timeFlipWrapper = timeFlipWrapper;
        this.messageSender = messageSender;
    }

    @Override
    public void run() {
        LOGGER.info("Status messages enabled.");
        int reportingInterval = INITIAL_REPORTING_INTERVAL;
        while (!isInterrupted()) {
            try {
                String macAddress = timeFlipWrapper.getAddress();
                int oldCalibrationVersion = timeFlipWrapper.readCalibrationVersion();
                int batteryLevel = timeFlipWrapper.readBatteryLevel();
                int rssi = timeFlipWrapper.getRSSI();

                StatusMessage statusMessage = new StatusMessage();
                statusMessage.setIdentifier(macAddress);
                statusMessage.setCalibrationVersion(oldCalibrationVersion);
                statusMessage.setBatteryLevel(batteryLevel);
                statusMessage.setRssi(rssi);

                StatusResponse statusResponse = messageSender.sendStatusMessage(statusMessage);

                if (statusResponse != null) {
                    int newReportingInterval = statusResponse.getReportingInterval();
                    LOGGER.info("Reporting interval from response is " + newReportingInterval + ".");
                    if (newReportingInterval > 0) {
                        LOGGER.info("Setting reporting interval to " + newReportingInterval + " ...");
                        reportingInterval = newReportingInterval;
                    }
                    else {
                        LOGGER.warning("Recieved value for reporting value is ignored since it is not a positive integer!");
                    }
                    int newCalibrationVersion = statusResponse.getCalibrationVersion();
                    LOGGER.info("Calibration version from response is " + newCalibrationVersion + ".");
                    if (newCalibrationVersion != oldCalibrationVersion) {
                        LOGGER.info("Writing new value of " + newCalibrationVersion + " to the calibration version characteristic ...");
                        if (timeFlipWrapper.writeCalibrationVersion(newCalibrationVersion)) {
                            LOGGER.info("New value successfully written.");
                        }
                        else {
                            LOGGER.warning("New value not successfully written!");
                        }
                    }
                    else {
                        LOGGER.info("No action required.");
                    }
                }
            }
            catch (BluetoothException e) {
                LOGGER.warning("A problem with the Bluetooth connection occurred!");
            }
            try {
                TimeUnit.SECONDS.sleep(reportingInterval);
            }
            catch (InterruptedException e) {
                interrupt();
            }
        }
        LOGGER.info("Status messages disabled.");
    }

}
