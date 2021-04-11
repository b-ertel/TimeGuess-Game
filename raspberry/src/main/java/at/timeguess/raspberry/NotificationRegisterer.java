package at.timeguess.raspberry;

import java.time.Duration;

import java.util.logging.Logger;

import tinyb.BluetoothDevice;
import tinyb.BluetoothGattCharacteristic;
import tinyb.BluetoothGattService;

import at.timeguess.raspberry.exceptions.NotificationRegisteringException;

import at.timeguess.raspberry.notifications.BatteryLevelNotification;
import at.timeguess.raspberry.notifications.ConnectedNotification;
import at.timeguess.raspberry.notifications.FacetsNotification;
import at.timeguess.raspberry.notifications.RSSINotification;

/**
 * A wrapper class around a TimeFlip device that provides convenience methods
 * for registering notifications.
 */
public class NotificationRegisterer {

    // default password for TimeFlip device
    private static final byte[] PASSWORD = {0x30, 0x30, 0x30, 0x30, 0x30, 0x30};

    private static final Long SERVICE_DISCOVERY_TIMEOUT = 10L;
    private static final Long CHARACTERISTIC_DISCOVERY_TIMEOUT = 10L;

    private final Logger logger = Logger.getLogger("at.timeguess.raspberry");

    private final BluetoothDevice timeflip;

    public NotificationRegisterer(BluetoothDevice timeflip) {
        this.timeflip = timeflip;
    }

    /**
     * Register a notification for the connection status of the device.
     * 
     * @param TIMEFLIP_MAC_ADDRESS the MAC address of the device
     * @param BACKEND_URL the URL of the backend
     * @throws NotificationRegisteringException
     */
    public void registerConnectedNotification(final String TIMEFLIP_MAC_ADDRESS, final String BACKEND_URL) throws NotificationRegisteringException {
        logger.info("Registering callback for connection status notifications ...");
        timeflip.enableConnectedNotifications(new ConnectedNotification(TIMEFLIP_MAC_ADDRESS, BACKEND_URL));
    }

    /**
     * Register a notification for the RSSI value of the device.
     * 
     * @param TIMEFLIP_MAC_ADDRESS the MAC address of the device
     * @param BACKEND_URL the URL of the backend
     * @throws NotificationRegisteringException
     */
    public void registerRSSINotification(final String TIMEFLIP_MAC_ADDRESS, final String BACKEND_URL) throws NotificationRegisteringException {
        logger.info("Registering callback for RSSI notifications ...");
        timeflip.enableRSSINotifications(new RSSINotification(TIMEFLIP_MAC_ADDRESS, BACKEND_URL));
    }

    /**
     * Register a notification for the battery level characteristic of the Battery service.
     * 
     * @param TIMEFLIP_MAC_ADDRESS the MAC address of the device
     * @param BACKEND_URL the URL of the backend
     * @throws NotificationRegisteringException
     */
    public void registerBatteryLevelNotification(final String TIMEFLIP_MAC_ADDRESS, final String BACKEND_URL) throws NotificationRegisteringException {

        logger.info("Searching for Battery Service ...");
        BluetoothGattService batteryService = timeflip.find(UUIDs.BATTERY_SERVICE, Duration.ofSeconds(SERVICE_DISCOVERY_TIMEOUT));
        if (batteryService != null) {
            logger.info("Battery Service found.");
        }
        else {
            logger.severe("Battery Service not found.");
            throw new NotificationRegisteringException();
        }

        logger.info("Searching for Battery level characteristic ...");
        BluetoothGattCharacteristic batteryLevelCharacteristic = batteryService.find(UUIDs.BATTERY_LEVEL_CHARACTERISTIC, Duration.ofSeconds(CHARACTERISTIC_DISCOVERY_TIMEOUT));
        if (batteryLevelCharacteristic != null) {
            logger.info("Battery level characteristic found.");
        }
        else {
            logger.severe("Battery level characteristic not found.");
            throw new NotificationRegisteringException();
        }

        logger.info("Registering for notifications from the battery level characteristic ...");
        batteryLevelCharacteristic.enableValueNotifications(new BatteryLevelNotification(TIMEFLIP_MAC_ADDRESS, BACKEND_URL));

    }

    /**
     * Register a notification for the facets characteristic of the TimeFlip service.
     * 
     * @param TIMEFLIP_MAC_ADDRESS the MAC address of the device
     * @param BACKEND_URL the URL of the backend
     * @throws NotificationRegisteringException
     */
    public void registerFacetsNotification(final String TIMEFLIP_MAC_ADDRESS, final String BACKEND_URL) throws NotificationRegisteringException {

        logger.info("Searching for TimeFlip service ...");
        BluetoothGattService timeflipService = timeflip.find(UUIDs.TIMEFLIP_SERVICE, Duration.ofSeconds(SERVICE_DISCOVERY_TIMEOUT));
        if (timeflipService != null) {
            logger.info("TimeFlip service found.");
        }
        else {
            logger.severe("TimeFlip service not found.");
            throw new NotificationRegisteringException();
        }

        logger.info("Searching for Password characteristic ...");
        BluetoothGattCharacteristic passwordCharacteristic = timeflipService.find(UUIDs.PASSWORD_CHARACTERISTIC, Duration.ofSeconds(CHARACTERISTIC_DISCOVERY_TIMEOUT));
        if (passwordCharacteristic != null) {
            logger.info("Password characteristic found.");
        }
        else {
            logger.severe("Password characteristic not found.");
            throw new NotificationRegisteringException();
        }

        logger.info("Writing password ...");
        if (passwordCharacteristic.writeValue(PASSWORD)) {
            logger.info("Password succesfully written.");
        }
        else {
            logger.severe("Problem writing password.");
            throw new NotificationRegisteringException();
        }

        logger.info("Searching for Calibration version characteristic ...");
        BluetoothGattCharacteristic calibrationVersionCharacteristic = timeflipService.find(UUIDs.CALIBRATION_VERSION_CHARACTERISTIC, Duration.ofSeconds(CHARACTERISTIC_DISCOVERY_TIMEOUT));
        if (calibrationVersionCharacteristic != null) {
            logger.info("Calibration version characteristic found.");
        }
        else {
            logger.severe("Calibration version characteristic not found.");
            throw new NotificationRegisteringException();
        }

        CalibrationVersionHelper calibrationVersionHelper = new CalibrationVersionHelper(calibrationVersionCharacteristic);

        logger.info("Searching for Facets characteristic ...");
        BluetoothGattCharacteristic facetsCharacteristic = timeflipService.find(UUIDs.FACETS_CHARACTERISTIC, Duration.ofSeconds(CHARACTERISTIC_DISCOVERY_TIMEOUT));
        if (facetsCharacteristic != null) {
            logger.info("Facets characteristic found.");
        }
        else {
            logger.severe("Facets characteristic not found.");
            throw new NotificationRegisteringException();
        }

        logger.info("Registering for notifications from the facets characteristic ...");
        facetsCharacteristic.enableValueNotifications(new FacetsNotification(TIMEFLIP_MAC_ADDRESS, BACKEND_URL, calibrationVersionHelper));

    }

}
