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
import tinyb.BluetoothManager;

/**
 * The class that contains the "main" method of the program
 * running on the Raspberry Pi.
 */
public final class Main {

    private static final String CONFIG_FILE = "config.txt";

    private static String timeflipMacAddress;
    private static String backendUrl;

    // timeout for finding devices
    private static final Long DEVICE_DISCOVERY_TIMEOUT = 10L;

    private Main() {

    }

    /**
     * The "main" method of the program running on the Raspberry Pi.
     * <p>
     * In order to run, a configuration file named "config.txt" is
     * required specifying the MAC address of the TimeFlip device and the
     * URL of the backend.
     * <p>
     * The program runs indefinitely but can be interrupted with Ctrl+C.
     */
    public static void main(String[] args) throws InterruptedException {
        readConfigFile();

        Logger logger = Logger.getLogger("at.timeguess.raspberry");
        ConsoleHandler consoleHandler = new ConsoleHandler();
        logger.addHandler(consoleHandler);
        logger.setUseParentHandlers(false);

        BluetoothManager bluetoothManager = BluetoothManager.getBluetoothManager();

        logger.info("Starting device discovery ...");
        if (bluetoothManager.startDiscovery()) {
            logger.info("Device discovery successfully started.");
        }
        else {
            logger.severe("Device discovery could not be started.");
            System.exit(-1);
        }

        logger.info("Searching for TimeFlip device with MAC address " + timeflipMacAddress + " ...");
        BluetoothDevice timeFlip = bluetoothManager.find(null,
                timeflipMacAddress,
                null,
                Duration.ofSeconds(DEVICE_DISCOVERY_TIMEOUT));
        if (timeFlip != null) {
            logger.info("TimeFlip device with MAC address " + timeflipMacAddress + " found.");
        }
        else {
            logger.severe("TimeFlip device with MAC address " + timeflipMacAddress + " not found.");
            System.exit(-1);
        }

        TimeFlipWrapper timeFlipWrapper = new TimeFlipWrapper(timeFlip);

        if (timeFlipWrapper.connect() &&
                timeFlipWrapper.findServicesAndCharacteristics() &&
                timeFlipWrapper.writePassword()) {
            logger.info("Connection established.");
        }
        else {
            logger.severe("Connection not established!");
            timeFlipWrapper.disconnect();
            System.exit(-1);
        }

        MessageSender messageSender = new MessageSender(backendUrl);

        timeFlipWrapper.enableFacetsMessages(messageSender);

        StatusThread statusThread = new StatusThread(timeFlipWrapper, messageSender);
        statusThread.start();

        ReconnectionThread reconnectionThread = new ReconnectionThread(timeFlipWrapper);
        reconnectionThread.start();

        Lock lock = new ReentrantLock();
        Condition condition = lock.newCondition();

        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
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
            condition.await();
        }
        finally {
            lock.unlock();
        }

        timeFlipWrapper.disconnect();
    }

    /**
     * Read the MAC address of the TimeFlip device and the URL of the backend
     * from the configuration file.
     */
    private static void readConfigFile() {
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(CONFIG_FILE);
        }
        catch (FileNotFoundException e) {
            System.err.println("Configuration file not found!");
            System.exit(-1);
        }

        Properties properties = new Properties();
        try {
            properties.load(inputStream);
        }
        catch (IOException e) {
            System.err.println("Error while reading from configuration file!");
            System.exit(-1);
        }

        timeflipMacAddress = properties.getProperty("TIMEFLIP_MAC_ADDRESS");
        if (timeflipMacAddress == null || timeflipMacAddress.isEmpty()) {
            System.err.println("MAC address of TimeFlip device not given in configuration file!");
            System.exit(-1);
        }

        backendUrl = properties.getProperty("BACKEND_URL");
        if (backendUrl == null || backendUrl.isEmpty()) {
            System.err.println("URL of backend not given in configuration file!");
            System.exit(-1);
        }
    }

}

