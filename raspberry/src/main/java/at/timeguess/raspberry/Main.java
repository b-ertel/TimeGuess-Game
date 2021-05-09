package at.timeguess.raspberry;

import java.util.Properties;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import java.util.logging.*;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import java.time.Duration;

import tinyb.BluetoothDevice;
import tinyb.BluetoothGattCharacteristic;
import tinyb.BluetoothGattService;
import tinyb.BluetoothManager;

/**
 * The class that contains the "main" method of the program
 * running on the Raspberry Pi.
 */
public final class Main {

    // the name of the configuration file
    private static final String CONFIG_FILE = "config.txt";

    // timeouts for finding devices, services and characteristics
    private static final Long DEVICE_DISCOVERY_TIMEOUT = 10L;
    private static final Long SERVICE_DISCOVERY_TIMEOUT = 10L;
    private static final Long CHARACTERISTIC_DISCOVERY_TIMEOUT = 10L;

    // default password for TimeFlip device
    private static final byte[] PASSWORD = {0x30, 0x30, 0x30, 0x30, 0x30, 0x30};

    private static String timeflipMacAddress;
    private static String backendUrl;

    static boolean running = true;

    private Main() {
    }

    /**
     * The "main" method of the program running on the Raspberry Pi.
     * <p>
     * In order to run, a configuration file named "config.txt" is
     * required specifying the MAC address of the TimeFlip device and the
     * URL of the backend.
     * <p>
     * The program searches for the TimeFlip device with the MAC address
     * specified in the configuration file. If found, an attempt is made to
     * connect to it and to find the battery level, facets and configuration version
     * characteristics. It is then starting a thread to send regular status
     * messages to the backend and process the response received. It is also listening
     * for changes of the facets characteristic, which also trigger messages to be
     * sent to the backend.
     * <p>
     * The program runs indefinitely but can be interrupted with Ctrl+C.
     */
    public static void main(String[] args) throws InterruptedException {

        Logger logger = Logger.getLogger("at.timeguess.raspberry");
        ConsoleHandler consoleHandler = new ConsoleHandler();
        logger.addHandler(consoleHandler);
        logger.setUseParentHandlers(false);

        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(CONFIG_FILE);
        }
        catch (FileNotFoundException e) {
            logger.severe("Configuration file not found!");
            System.exit(-1);
        }

        Properties properties = new Properties();
        try {
            properties.load(inputStream);
        }
        catch (IOException e) {
            logger.severe("Error while reading from configuration file!");
            System.exit(-1);
        }

        timeflipMacAddress = properties.getProperty("TIMEFLIP_MAC_ADDRESS");
        if (timeflipMacAddress == null || timeflipMacAddress.isEmpty()) {
            logger.severe("MAC address of TimeFlip device not given in configuration file!");
            System.exit(-1);
        }

        backendUrl = properties.getProperty("BACKEND_URL");
        if (backendUrl == null || backendUrl.isEmpty()) {
            logger.severe("URL of backend not given in configuration file!");
            System.exit(-1);
        }

        BluetoothManager manager = BluetoothManager.getBluetoothManager();

        logger.info("Starting device discovery ...");
        if (manager.startDiscovery()) {
            logger.info("Device discovery successfully started.");
        }
        else {
            logger.severe("Device discovery could not be started.");
            System.exit(-1);
        }

        logger.info("Searching for TimeFlip device with MAC address " + timeflipMacAddress + " ...");
        BluetoothDevice timeflip = manager.find(null, timeflipMacAddress, null, Duration.ofSeconds(DEVICE_DISCOVERY_TIMEOUT));
        if (timeflip != null) {
            logger.info("TimeFlip device with MAC address " + timeflipMacAddress + " found.");
        }
        else {
            logger.severe("TimeFlip device with MAC address " + timeflipMacAddress + " not found.");
            System.exit(-1);
        }

        logger.info("Connecting to TimeFlip device with MAC address " + timeflipMacAddress + " ...");
        if (timeflip.connect()) {
            logger.info("Connection established.");
        }
        else {
            logger.severe("Connection not established.");
            System.exit(-1);
        }

        logger.info("Searching for Battery Service ...");
        BluetoothGattService batteryService = timeflip.find(UUIDs.BATTERY_SERVICE, Duration.ofSeconds(SERVICE_DISCOVERY_TIMEOUT));
        if (batteryService != null) {
            logger.info("Battery Service found.");
        }
        else {
            logger.severe("Battery Service not found.");
            timeflip.disconnect();
            System.exit(-1);
        }

        logger.info("Searching for Battery level characteristic ...");
        BluetoothGattCharacteristic batteryLevelCharacteristic = batteryService.find(UUIDs.BATTERY_LEVEL_CHARACTERISTIC, Duration.ofSeconds(CHARACTERISTIC_DISCOVERY_TIMEOUT));
        if (batteryLevelCharacteristic != null) {
            logger.info("Battery level characteristic found.");
        }
        else {
            logger.severe("Battery level characteristic not found.");
            timeflip.disconnect();
            System.exit(-1);
        }

        logger.info("Searching for TimeFlip service ...");
        BluetoothGattService timeflipService = timeflip.find(UUIDs.TIMEFLIP_SERVICE, Duration.ofSeconds(SERVICE_DISCOVERY_TIMEOUT));
        if (timeflipService != null) {
            logger.info("TimeFlip service found.");
        }
        else {
            logger.severe("TimeFlip service not found.");
            timeflip.disconnect();
            System.exit(-1);
        }

        logger.info("Searching for Password characteristic ...");
        BluetoothGattCharacteristic passwordCharacteristic = timeflipService.find(UUIDs.PASSWORD_CHARACTERISTIC, Duration.ofSeconds(CHARACTERISTIC_DISCOVERY_TIMEOUT));
        if (passwordCharacteristic != null) {
            logger.info("Password characteristic found.");
        }
        else {
            logger.severe("Password characteristic not found.");
            timeflip.disconnect();
            System.exit(-1);
        }

        logger.info("Writing password ...");
        if (passwordCharacteristic.writeValue(PASSWORD)) {
            logger.info("Password succesfully written.");
        }
        else {
            logger.severe("Problem writing password.");
            timeflip.disconnect();
            System.exit(-1);
        }

        logger.info("Searching for Facets characteristic ...");
        BluetoothGattCharacteristic facetsCharacteristic = timeflipService.find(UUIDs.FACETS_CHARACTERISTIC, Duration.ofSeconds(CHARACTERISTIC_DISCOVERY_TIMEOUT));
        if (facetsCharacteristic != null) {
            logger.info("Facets characteristic found.");
        }
        else {
            logger.severe("Facets characteristic not found.");
            timeflip.disconnect();
            System.exit(-1);
        }

        logger.info("Searching for Calibration version characteristic ...");
        BluetoothGattCharacteristic calibrationVersionCharacteristic = timeflipService.find(UUIDs.CALIBRATION_VERSION_CHARACTERISTIC, Duration.ofSeconds(CHARACTERISTIC_DISCOVERY_TIMEOUT));
        if (calibrationVersionCharacteristic != null) {
            logger.info("Calibration version characteristic found.");
        }
        else {
            logger.severe("Calibration version characteristic not found.");
            timeflip.disconnect();
            System.exit(-1);
        }

        CalibrationVersionHelper calibrationVersionHelper = new CalibrationVersionHelper(calibrationVersionCharacteristic);

        logger.info("Enable status messages ...");
        StatusMessageSender statusMessageSender = new StatusMessageSender(timeflipMacAddress, backendUrl, calibrationVersionHelper, batteryLevelCharacteristic, timeflip);
        statusMessageSender.start();

        logger.info("Enable facets messages ...");
        facetsCharacteristic.enableValueNotifications(new FacetsMessageSender(timeflipMacAddress, backendUrl));

        Lock lock = new ReentrantLock();
        Condition condition = lock.newCondition();

        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                running = false;
                lock.lock();
                try {
                    condition.signalAll();
                }
                finally {
                    lock.unlock();
                }
            }
        });

        lock.lock();
        try {
            while (running) {
                condition.await();
            }
        }
        finally {
            lock.unlock();
        }

        timeflip.disconnect();

    }

}
