package at.timeguess.raspberry;

import java.util.logging.Logger;

import java.time.Duration;

import tinyb.BluetoothDevice;
import tinyb.BluetoothException;
import tinyb.BluetoothGattCharacteristic;
import tinyb.BluetoothGattService;

/**
 * A wrapper class for TimeFlip devices that provides an additional layer
 * of abstraction over the more general TinyB classes and is specifically adapted to the needs
 * of communicating with TimeFlip devices.
 */
public class TimeFlipWrapper {

    // UUIDs of services
    public static final String BATTERY_SERVICE = "0000180f-0000-1000-8000-00805f9b34fb";
    public static final String TIMEFLIP_SERVICE = "f1196f50-71a4-11e6-bdf4-0800200c9a66";

    // UUIDs of characteristics
    public static final String BATTERY_LEVEL_CHARACTERISTIC = "00002a19-0000-1000-8000-00805f9b34fb";
    public static final String FACETS_CHARACTERISTIC = "f1196f52-71a4-11e6-bdf4-0800200c9a66";
    public static final String CALIBRATION_VERSION_CHARACTERISTIC = "f1196f56-71a4-11e6-bdf4-0800200c9a66";
    public static final String PASSWORD_CHARACTERISTIC = "f1196f57-71a4-11e6-bdf4-0800200c9a66";

    // default password for TimeFlip devices
    public static final byte[] PASSWORD = {0x30, 0x30, 0x30, 0x30, 0x30, 0x30};

    private BluetoothDevice timeFlip;

    private BluetoothGattService batteryService;
    private BluetoothGattService timeflipService;

    private BluetoothGattCharacteristic batteryLevelCharacteristic;
    private BluetoothGattCharacteristic facetsCharacteristic;
    private BluetoothGattCharacteristic calibrationVersionCharacteristic;
    private BluetoothGattCharacteristic passwordCharacteristic;

    // timeouts for finding services and characteristics
    private static final Long SERVICE_DISCOVERY_TIMEOUT = 10L;
    private static final Long CHARACTERISTIC_DISCOVERY_TIMEOUT = 10L;

    private static final Logger LOGGER = Logger.getLogger("at.timeguess.raspberry");

    public TimeFlipWrapper(BluetoothDevice timeFlip) {
        this.timeFlip = timeFlip;
    }

    /**
     * Connect to the TimeFlip device.
     * 
     * @return a boolean indicating if the connection was successful
     */
    public boolean connect() {
        try {
            return timeFlip.connect();
        }
        catch (BluetoothException e) {
            return false;
        }
    }

    /**
     * Disconnect from the TimeFlip device.
     * 
     * @return a boolean indicating if the disconnection was successful
     */
    public boolean disconnect() {
        try {
            return timeFlip.disconnect();
        }
        catch (BluetoothException e) {
            return false;
        }
    }

    /**
     * Check the connection status of the TimeFlip device.
     * 
     * @return a boolean indicating if the TimeFlip device is connected
     */
    public boolean getConnected() {
        return timeFlip.getConnected();
    }

    /**
     * Find the required services and characteristics of the TimeFlip device.
     * 
     * @return a boolean indicating if all services and characteristics could be found
     */
    public boolean findServicesAndCharacteristics() {
        LOGGER.info("Searching for Battery Service ...");
        batteryService = timeFlip.find(BATTERY_SERVICE, Duration.ofSeconds(SERVICE_DISCOVERY_TIMEOUT));
        if (batteryService != null) {
            LOGGER.info("Battery Service found.");
        }
        else {
            LOGGER.severe("Battery Service not found.");
            return false;
        }

        LOGGER.info("Searching for TimeFlip service ...");
        timeflipService = timeFlip.find(TIMEFLIP_SERVICE, Duration.ofSeconds(SERVICE_DISCOVERY_TIMEOUT));
        if (timeflipService != null) {
            LOGGER.info("TimeFlip service found.");
        }
        else {
            LOGGER.severe("TimeFlip service not found.");
            return false;
        }

        LOGGER.info("Searching for Battery level characteristic ...");
        batteryLevelCharacteristic = batteryService.find(BATTERY_LEVEL_CHARACTERISTIC, Duration.ofSeconds(CHARACTERISTIC_DISCOVERY_TIMEOUT));
        if (batteryLevelCharacteristic != null) {
            LOGGER.info("Battery level characteristic found.");
        }
        else {
            LOGGER.severe("Battery level characteristic not found.");
            return false;
        }

        LOGGER.info("Searching for Facets characteristic ...");
        facetsCharacteristic = timeflipService.find(FACETS_CHARACTERISTIC, Duration.ofSeconds(CHARACTERISTIC_DISCOVERY_TIMEOUT));
        if (facetsCharacteristic != null) {
            LOGGER.info("Facets characteristic found.");
        }
        else {
            LOGGER.severe("Facets characteristic not found.");
            return false;
        }

        LOGGER.info("Searching for Calibration version characteristic ...");
        calibrationVersionCharacteristic = timeflipService.find(CALIBRATION_VERSION_CHARACTERISTIC, Duration.ofSeconds(CHARACTERISTIC_DISCOVERY_TIMEOUT));
        if (calibrationVersionCharacteristic != null) {
            LOGGER.info("Calibration version characteristic found.");
        }
        else {
            LOGGER.severe("Calibration version characteristic not found.");
            return false;
        }

        LOGGER.info("Searching for Password characteristic ...");
        passwordCharacteristic = timeflipService.find(PASSWORD_CHARACTERISTIC, Duration.ofSeconds(CHARACTERISTIC_DISCOVERY_TIMEOUT));
        if (passwordCharacteristic != null) {
            LOGGER.info("Password characteristic found.");
        }
        else {
            LOGGER.severe("Password characteristic not found.");
            return false;
        }

        return true;
    }

    /**
     * Write the password to the password characteristic.
     * 
     * @return a boolean indicating if writing the password was successful
     */
    public boolean writePassword() {
        try {
            return passwordCharacteristic.writeValue(PASSWORD);
        }
        catch (BluetoothException e) {
            return false;
        }
    }

    public String getAddress() {
        return timeFlip.getAddress();
    }

    /**
     * Read the current value of the Calibration Version characteristic.
     * 
     * @return the current calibration version
     */
    public int readCalibrationVersion() {
        byte[] calibrationVersionRaw = calibrationVersionCharacteristic.readValue();
        int calibrationVersion = byteArrayToInt(calibrationVersionRaw);
        return calibrationVersion;
    }

    /**
     * Write a new value to the Calibration version characteristic.
     * 
     * @param calibrationVersion the new value
     * @return a boolean indicating if the new value was successfully written
     */
    public boolean writeCalibrationVersion(int calibrationVersion) {
        byte[] calibrationVersionRaw = intToByteArray(calibrationVersion);
        return calibrationVersionCharacteristic.writeValue(calibrationVersionRaw);
    }

    public int readBatteryLevel() {
        byte[] batteryLevelRaw = batteryLevelCharacteristic.readValue();
        return batteryLevelRaw[0];
    }

    public int getRSSI() {
        return timeFlip.getRSSI();
    }

    /**
     * Enable facets messages for the TimeFlip device.
     * 
     * @param messageSender the {@link MessageSender} to be used for sending the facets messages
     */
    public void enableFacetsMessages(MessageSender messageSender) {
        LOGGER.info("Enable facets messages ...");
        facetsCharacteristic.enableValueNotifications(new FacetsCharacteristicBluetoothNotification(getAddress(), messageSender));
    }

    /**
     * Disable facets messages for the TimeFlip device.
     */
    public void disableFacetsMessages() {
        LOGGER.info("Disable facets messages ...");
        facetsCharacteristic.disableValueNotifications();
    }

    /**
     * Convert a byte array of length 4 to an int using little endian.
     * 
     * @param in the byte array to convert
     * @return the result of the conversion
     */
    private int byteArrayToInt(byte[] in) {
        int out = 0;
        for (int b = 0; b < 4; b++) {
            out |= (in[b] & 0xff) << (8 * b);
        }
        return out;
    }

    /**
     * Convert an int to a byte array of length 4 using little endian.
     * 
     * @param in the int to convert
     * @return the result of the conversion
     */
    private byte[] intToByteArray(int in) {
        byte[] out = new byte[4];
        for (int b = 0; b < 4; b++) {
            out[b] = (byte) in;
            in >>= 8;
        }
        return out;
    }

}
