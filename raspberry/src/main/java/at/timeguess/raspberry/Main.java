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
import tinyb.BluetoothManager;

import at.timeguess.raspberry.exceptions.NotificationRegisteringException;

/**
 * Entry point for program to search for Bluetooth devices and communicate with them
 */
public final class Main {

    private static final String LOG_FILE = "log.txt";
    private static final String CONFIG_FILE = "config.txt";

    private static String TIMEFLIP_MAC_ADDRESS;
    private static String BACKEND_URL;

    private static final Long DEVICE_DISCOVERY_TIMEOUT = 10L;

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

        // register for all kinds of notifications from TimeFlip device
        NotificationRegisterer notificationRegisterer = new NotificationRegisterer(timeflip);
        try {
            notificationRegisterer.registerConnectedNotification(TIMEFLIP_MAC_ADDRESS, BACKEND_URL);
            notificationRegisterer.registerRSSINotification(TIMEFLIP_MAC_ADDRESS, BACKEND_URL);
            notificationRegisterer.registerBatteryLevelNotification(TIMEFLIP_MAC_ADDRESS, BACKEND_URL);
            notificationRegisterer.registerFacetsNotification(TIMEFLIP_MAC_ADDRESS, BACKEND_URL);
        }
        catch (NotificationRegisteringException e) {
            logger.severe("A problem occured while registering notifications.");
            timeflip.disconnect();
            System.exit(-1);
        }

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
