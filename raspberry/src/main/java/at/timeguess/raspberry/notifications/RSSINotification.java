package at.timeguess.raspberry.notifications;

import java.util.logging.*;

import tinyb.BluetoothNotification;

/**
 * A BluetoothNotification for the RSSI property of the TimeFlip device.
 */
public class RSSINotification implements BluetoothNotification<Short> {

    private Logger logger = Logger.getLogger("at.timeguess.raspberry.notifications");

    /**
     * This method gets called whenever a notification about
     * a change of the RSSI property is issued.
     */
    public void run(Short RSSI) {
        logger.info("TimeFlip device's RSSI has changed to " + RSSI + ".");
    }

}
