package at.timeguess.raspberry.notifications;

import java.util.logging.*;

import tinyb.BluetoothNotification;

/**
 * A BluetoothNotification for the facets characteristic.
 */
public class FacetsNotification implements BluetoothNotification<byte[]> {

    private Logger logger = Logger.getLogger("at.timeguess.raspberry.notifications");

    /**
     * This method gets called whenever a notification about
     * a change of the facets characteristic is issued.
     */
    public void run(byte[] facetRaw) {
        logger.info(String.format("Facets characteristic reported new value 0x%02x.", facetRaw[0]));
    }

}
