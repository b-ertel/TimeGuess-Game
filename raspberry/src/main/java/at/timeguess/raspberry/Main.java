package at.timeguess.raspberry;

import tinyb.BluetoothDevice;
import tinyb.BluetoothException;
import tinyb.BluetoothManager;
import tinyb.BluetoothGattService;
import tinyb.BluetoothGattCharacteristic;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import java.util.logging.*;

import at.timeguess.raspberry.notifications.BatteryLevelNotification;
import at.timeguess.raspberry.notifications.ConnectedNotification;
import at.timeguess.raspberry.notifications.FacetsNotification;
import at.timeguess.raspberry.notifications.RSSINotification;

import java.io.IOException;

import java.time.Duration;

/**
 * Entry point for program to search for Bluetooth devices and communicate with them
 */
public final class Main {

    // write logs to file "log.txt" in the user's home directory
    private static final String LOG_FILE = "%h/log.txt";

    private static final Long DEVICE_DISCOVERY_TIMEOUT = 10L;
    private static final Long SERVICE_DISCOVERY_TIMEOUT = 10L;
    private static final Long CHARACTERISTIC_DISCOVERY_TIMEOUT = 10L;

    // UUIDs Device Information Service
    private static final String DEVICE_INFORMATION_SERVICE = "0000180a-0000-1000-8000-00805f9b34fb";
    private static final String FIRMWARE_REVISION_STRING_CHARACTERISTIC = "00002a26-0000-1000-8000-00805f9b34fb";

    // UUIDs Battery Service
    private static final String BATTERY_SERVICE = "0000180f-0000-1000-8000-00805f9b34fb";
    private static final String BATTERY_LEVEL_CHARACTERISTIC = "00002a19-0000-1000-8000-00805f9b34fb";

    // UUIDs TimeFlip service
    private static final String TIMEFLIP_SERVICE = "f1196f50-71a4-11e6-bdf4-0800200c9a66";
    private static final String ACCELEROMETER_DATA_CHARACTERISTIC = "f1196f51-71a4-11e6-bdf4-0800200c9a66";
    private static final String FACETS_CHARACTERISTIC = "f1196f52-71a4-11e6-bdf4-0800200c9a66";
    private static final String COMMAND_RESULT_OUTPUT_CHARACTERISTIC = "f1196f53-71a4-11e6-bdf4-0800200c9a66";
    private static final String COMMAND_CHARACTERISTIC = "f1196f54-71a4-11e6-bdf4-0800200c9a66";
    private static final String DOUBLE_TAP_DEFINITION_CHARACTERISTIC = "f1196f55-71a4-11e6-bdf4-0800200c9a66";
    private static final String CALIBRATION_VERSION_CHARACTERISTIC = "f1196f56-71a4-11e6-bdf4-0800200c9a66";
    private static final String PASSWORD_CHARACTERISTIC = "f1196f57-71a4-11e6-bdf4-0800200c9a66";

    // default password
    private static final byte[] PASSWORD = {0x30, 0x30, 0x30, 0x30, 0x30, 0x30};

    static boolean running = true;

    private Main() {
    }

    /**
     * This program should connect to TimeFlip devices and read the facet characteristic exposed by the devices
     * over Bluetooth Low Energy.
     *
     * @param args the program arguments
     * @throws InterruptedException
     * @see <a href="https://github.com/DI-GROUP/TimeFlip.Docs/blob/master/Hardware/BLE_device_commutication_protocol_v3.0_en.md" target="_top">BLE device communication protocol v3.0</a>
     */
    public static void main(String[] args) throws InterruptedException {

        if (args.length < 1) {
            System.err.println("Run with the MAC address of the TimeFlip device as parameter!");
            System.exit(-1);
        }

        Logger logger = Logger.getLogger("at.timeguess.raspberry");

        ConsoleHandler consoleHandler = new ConsoleHandler();
        logger.addHandler(consoleHandler);

        logger.setUseParentHandlers(false);

        try {
            FileHandler filehandler = new FileHandler(LOG_FILE);
            filehandler.setFormatter(new XMLFormatter());
            logger.addHandler(filehandler);
            System.out.println("***");
            System.out.println("Logs will be written to standard error and to file \"log.txt\" in user's home directory.");
            System.out.println("***");
        } catch (IOException e) {
            System.out.println("***");
            System.out.println("Logs will be written to standard error.");
            System.out.println("***");
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

        logger.info("Searching for TimeFlip device with MAC address " + args[0] + " ...");
        BluetoothDevice timeflip = manager.find(null, args[0], null, Duration.ofSeconds(DEVICE_DISCOVERY_TIMEOUT));
        if (timeflip != null) {
            logger.info("TimeFlip device with MAC address " + args[0] + " found.");
        }
        else {
            logger.severe("TimeFlip device with MAC address " + args[0] + " not found.");
            System.exit(-1);
        }

        if (manager.getDiscovering()) {
            logger.warning("Device discovery is still on after the device has been found.");
        }

        logger.info("Connecting to TimeFlip device with MAC address " + args[0] + " ...");
        if (timeflip.connect()) {
            logger.info("Connection established.");
        }
        else {
            logger.severe("Connection not established.");
            System.exit(-1);
        }

        logger.info("Registering callback for connection status notifications ...");
        timeflip.enableConnectedNotifications(new ConnectedNotification());

        logger.info("Registering callback for RSSI notifications ...");
        timeflip.enableRSSINotifications(new RSSINotification());

        logger.info("Searching for Battery Service ...");
        BluetoothGattService batteryService = timeflip.find(BATTERY_SERVICE, Duration.ofSeconds(SERVICE_DISCOVERY_TIMEOUT));
        if (batteryService != null) {
            logger.info("Battery Service found.");
        }
        else {
            logger.severe("Battery Service not found.");
            timeflip.disconnect();
            System.exit(-1);
        }

        logger.info("Searching for Battery level characteristic ...");
        BluetoothGattCharacteristic batteryLevelCharacteristic = batteryService.find(BATTERY_LEVEL_CHARACTERISTIC, Duration.ofSeconds(CHARACTERISTIC_DISCOVERY_TIMEOUT));
        if (batteryLevelCharacteristic != null) {
            logger.info("Battery level characteristic found.");
        }
        else {
            logger.severe("Battery level characteristic not found.");
            timeflip.disconnect();
            System.exit(-1);
        }

        logger.info("Registering for notifications from the battery level characteristic ...");
        batteryLevelCharacteristic.enableValueNotifications(new BatteryLevelNotification());

        logger.info("Searching for TimeFlip service ...");
        BluetoothGattService timeflipService = timeflip.find(TIMEFLIP_SERVICE, Duration.ofSeconds(SERVICE_DISCOVERY_TIMEOUT));
        if (timeflipService != null) {
            logger.info("TimeFlip service found.");
        }
        else {
            logger.severe("TimeFlip service not found.");
            timeflip.disconnect();
            System.exit(-1);
        }

        logger.info("Searching for Password characteristic ...");
        BluetoothGattCharacteristic passwordCharacteristic = timeflipService.find(PASSWORD_CHARACTERISTIC, Duration.ofSeconds(CHARACTERISTIC_DISCOVERY_TIMEOUT));
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
        BluetoothGattCharacteristic facetsCharacteristic = timeflipService.find(FACETS_CHARACTERISTIC, Duration.ofSeconds(CHARACTERISTIC_DISCOVERY_TIMEOUT));
        if (facetsCharacteristic != null) {
            logger.info("Facets characteristic found.");
        }
        else {
            logger.severe("Facets characteristic not found.");
            timeflip.disconnect();
            System.exit(-1);
        }

        logger.info("Registering for notifications from the facets characteristic ...");
        facetsCharacteristic.enableValueNotifications(new FacetsNotification());

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
