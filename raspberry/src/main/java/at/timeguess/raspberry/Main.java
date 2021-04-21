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
import tinyb.BluetoothException;
import tinyb.BluetoothGattCharacteristic;
import tinyb.BluetoothGattService;
import tinyb.BluetoothManager;

import kong.unirest.Unirest;

/**
 * Entry point for program to search for Bluetooth devices and communicate with them
 */
public final class Main {

    private static final String LOG_FILE = "log.txt";
    private static final String CONFIG_FILE = "config.txt";

    private static final Long DEVICE_DISCOVERY_TIMEOUT = 10L;
    private static final Long SERVICE_DISCOVERY_TIMEOUT = 10L;
    private static final Long CHARACTERISTIC_DISCOVERY_TIMEOUT = 10L;

    // default password for TimeFlip device
    private static final byte[] PASSWORD = {0x30, 0x30, 0x30, 0x30, 0x30, 0x30};

    private static String TIMEFLIP_MAC_ADDRESS;
    private static String BACKEND_URL;

    static boolean running = true;

    private Main() {
    }

    /**
     * This program should connect to TimeFlip devices and read the facet characteristic exposed by the devices
     * over Bluetooth Low Energy.
     *
     * @throws InterruptedException
     * @see <a href="https://github.com/DI-GROUP/TimeFlip.Docs/blob/master/Hardware/BLE_device_commutication_protocol_v3.0_en.md" target="_top">BLE device communication protocol v3.0</a>
     */
    public static void main(String[] args) throws InterruptedException {

        // set up logging
        Logger logger = Logger.getLogger("at.timeguess.raspberry");
        ConsoleHandler consoleHandler = new ConsoleHandler();
        logger.addHandler(consoleHandler);
        logger.setUseParentHandlers(false);
        try {
            FileHandler filehandler = new FileHandler(LOG_FILE);
            filehandler.setFormatter(new XMLFormatter());
            logger.addHandler(filehandler);
            System.out.println("***");
            System.out.println("Logs will be written to standard error and to the file \"log.txt\" in this folder.");
            System.out.println("***");
        }
        catch (IOException e) {
            System.out.println("***");
            System.out.println("Logs will be written to standard error only since the file \"log.txt\" in this folder could not be opened.");
            System.out.println("***");
        }

        // read from configuration file
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
        TIMEFLIP_MAC_ADDRESS = properties.getProperty("TIMEFLIP_MAC_ADDRESS");
        if (TIMEFLIP_MAC_ADDRESS == null || TIMEFLIP_MAC_ADDRESS.isEmpty()) {
            logger.severe("MAC address of TimeFlip device not given in configuration file!");
            System.exit(-1);
        }
        BACKEND_URL = properties.getProperty("BACKEND_URL");
        if (BACKEND_URL == null || BACKEND_URL.isEmpty()) {
            logger.severe("URL of backend not given in configuration file!");
            System.exit(-1);
        }

        BluetoothManager manager = BluetoothManager.getBluetoothManager();

        logger.info("Starting device discovery ...");
        try {
            if (manager.startDiscovery()) {
                logger.info("Device discovery successfully started.");
            }
            else {
                logger.severe("Device discovery could not be started.");
                System.exit(-1);
            }
        }
        catch (BluetoothException e) {
            logger.severe("Device discovery could not be started.");
            System.exit(-1);
        }

        logger.info("Searching for TimeFlip device with MAC address " + TIMEFLIP_MAC_ADDRESS + " ...");
        BluetoothDevice timeflip = manager.find(null, TIMEFLIP_MAC_ADDRESS, null, Duration.ofSeconds(DEVICE_DISCOVERY_TIMEOUT));
        if (timeflip != null) {
            logger.info("TimeFlip device with MAC address " + TIMEFLIP_MAC_ADDRESS + " found.");
        }
        else {
            logger.severe("TimeFlip device with MAC address " + TIMEFLIP_MAC_ADDRESS + " not found.");
            System.exit(-1);
        }

        if (manager.getDiscovering()) {
            logger.warning("Device discovery is still on after the device has been found.");
        }

        logger.info("Connecting to TimeFlip device with MAC address " + TIMEFLIP_MAC_ADDRESS + " ...");
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
        StatusMessageSender statusMessageSender = new StatusMessageSender(TIMEFLIP_MAC_ADDRESS, BACKEND_URL, calibrationVersionHelper, batteryLevelCharacteristic, timeflip);
        statusMessageSender.start();

        logger.info("Enable facets messages ...");
        facetsCharacteristic.enableValueNotifications(new FacetsMessageSender(TIMEFLIP_MAC_ADDRESS, BACKEND_URL, calibrationVersionHelper));

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
