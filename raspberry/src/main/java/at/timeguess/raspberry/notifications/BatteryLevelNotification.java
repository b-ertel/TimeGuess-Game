package at.timeguess.raspberry.notifications;

import java.util.logging.*;

import tinyb.BluetoothNotification;

/**
 * A BluetoothNotification for the battery level characteristic.
 */
public class BatteryLevelNotification implements BluetoothNotification<byte[]> {

    private Logger logger = Logger.getLogger("at.timeguess.raspberry.notifications");

    /**
     * This method gets called whenever a notification about
     * a change of the battery level characteristic is issued.
     */
    public void run(byte[] batteryLevelRaw) {
        logger.info(String.format("Battery level characteristic reported new value 0x%02x.", batteryLevelRaw[0]));
    }

}
