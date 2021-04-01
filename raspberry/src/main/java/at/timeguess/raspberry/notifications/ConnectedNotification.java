package at.timeguess.raspberry.notifications;

import java.util.logging.*;

import tinyb.BluetoothNotification;

/**
 * A BluetoothNotification for the connected property of the TimeFlip device.
 */
public class ConnectedNotification implements BluetoothNotification<Boolean> {

    private Logger logger = Logger.getLogger("at.timeguess.raspberry.notifications");

    /**
     * This method gets called whenever a notification about
     * a change of the connected property is issued.
     */
    public void run(Boolean connected) {
        logger.info("TimeFlip device " + (connected ? "connected" : "disconnected") + ".");
    }

}
