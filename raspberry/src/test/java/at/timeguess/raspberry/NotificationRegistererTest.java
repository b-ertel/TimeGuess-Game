package at.timeguess.raspberry;

import org.junit.Assert;
import org.junit.Test;

import tinyb.BluetoothDevice;
import tinyb.BluetoothGattCharacteristic;
import tinyb.BluetoothGattService;

import org.mockito.Mockito;

import at.timeguess.raspberry.exceptions.NotificationRegisteringException;

import static org.mockito.Mockito.when;

import java.time.Duration;

public class NotificationRegistererTest {

    // default password for TimeFlip device
    private static final byte[] PASSWORD = {0x30, 0x30, 0x30, 0x30, 0x30, 0x30};

    @Test
    public void registerConnectedNotificationTest() throws NotificationRegisteringException {

        // mock TimeFlip device
        BluetoothDevice timeflip = Mockito.mock(BluetoothDevice.class);

        // create a notification registerer for the mocked TimeFlip device
        NotificationRegisterer notificationRegisterer = new NotificationRegisterer(timeflip);

        // actual tests
        Assert.assertNotNull(notificationRegisterer);
        notificationRegisterer.registerConnectedNotification("TIMEFLIP_MAC_ADDRESS", "BACKEND_URL");

    }

    @Test
    public void registerRSSINotificationTest() throws NotificationRegisteringException {

        // mock TimeFlip device
        BluetoothDevice timeflip = Mockito.mock(BluetoothDevice.class);

        // create a notification registerer for the mocked TimeFlip device
        NotificationRegisterer notificationRegisterer = new NotificationRegisterer(timeflip);

        // actual tests
        Assert.assertNotNull(notificationRegisterer);
        notificationRegisterer.registerRSSINotification("TIMEFLIP_MAC_ADDRESS", "BACKEND_URL");

    }
    
    @Test
    public void registerBatteryLevelNotificationTest() throws NotificationRegisteringException {

        // mock TimeFlip device
        BluetoothGattCharacteristic batteryLevelCharacteristic = Mockito.mock(BluetoothGattCharacteristic.class);
        BluetoothGattService batteryService = Mockito.mock(BluetoothGattService.class);
        when(batteryService.find(Mockito.eq(UUIDs.BATTERY_LEVEL_CHARACTERISTIC), Mockito.any(Duration.class)))
                .thenReturn(batteryLevelCharacteristic);
        BluetoothDevice timeflip = Mockito.mock(BluetoothDevice.class);
        when(timeflip.find(Mockito.eq(UUIDs.BATTERY_SERVICE), Mockito.any(Duration.class)))
                .thenReturn(batteryService);

        // create a notification registerer for the mocked TimeFlip device
        NotificationRegisterer notificationRegisterer = new NotificationRegisterer(timeflip);

        // actual tests
        Assert.assertNotNull(notificationRegisterer);
        notificationRegisterer.registerBatteryLevelNotification("TIMEFLIP_MAC_ADDRESS", "BACKEND_URL");

    }

    @Test
    public void registerBatteryLevelNotificationNoServiceTest() throws NotificationRegisteringException {

        // mock TimeFlip device
        BluetoothDevice timeflip = Mockito.mock(BluetoothDevice.class);
        when(timeflip.find(Mockito.eq(UUIDs.BATTERY_SERVICE), Mockito.any(Duration.class)))
                .thenReturn(null);

        // create a notification registerer for the mocked TimeFlip device
        NotificationRegisterer notificationRegisterer = new NotificationRegisterer(timeflip);

        // actual tests
        Assert.assertNotNull(notificationRegisterer);
        Assert.assertThrows(
                NotificationRegisteringException.class,
                () -> notificationRegisterer.registerBatteryLevelNotification("TIMEFLIP_MAC_ADDRESS", "BACKEND_URL")
        );

    }

    @Test
    public void registerBatteryLevelNotificationNoCharacteristicTest() throws NotificationRegisteringException {

        // mock TimeFlip device
        BluetoothGattService batteryService = Mockito.mock(BluetoothGattService.class);
        when(batteryService.find(Mockito.eq(UUIDs.BATTERY_LEVEL_CHARACTERISTIC), Mockito.any(Duration.class)))
                .thenReturn(null);
        BluetoothDevice timeflip = Mockito.mock(BluetoothDevice.class);
        when(timeflip.find(Mockito.eq(UUIDs.BATTERY_SERVICE), Mockito.any(Duration.class)))
                .thenReturn(batteryService);

        // create a notification registerer for the mocked TimeFlip device
        NotificationRegisterer notificationRegisterer = new NotificationRegisterer(timeflip);

        // actual tests
        Assert.assertNotNull(notificationRegisterer);
        Assert.assertThrows(
                NotificationRegisteringException.class,
                () -> notificationRegisterer.registerBatteryLevelNotification("TIMEFLIP_MAC_ADDRESS", "BACKEND_URL")
        );

    }

    @Test
    public void registerFacetsNotificationTest() throws NotificationRegisteringException {

        // mock TimeFlip device
        BluetoothGattCharacteristic passwordCharacteristic = Mockito.mock(BluetoothGattCharacteristic.class);
        when(passwordCharacteristic.writeValue(PASSWORD))
                .thenReturn(true);
        BluetoothGattCharacteristic facetsCharacteristic = Mockito.mock(BluetoothGattCharacteristic.class);
        BluetoothGattCharacteristic calibrationVersionCharacteristic = Mockito.mock(BluetoothGattCharacteristic.class);
        BluetoothGattService timeflipService = Mockito.mock(BluetoothGattService.class);
        when(timeflipService.find(Mockito.eq(UUIDs.PASSWORD_CHARACTERISTIC), Mockito.any(Duration.class)))
                .thenReturn(passwordCharacteristic);
        when(timeflipService.find(Mockito.eq(UUIDs.FACETS_CHARACTERISTIC), Mockito.any(Duration.class)))
                .thenReturn(facetsCharacteristic);
        when(timeflipService.find(Mockito.eq(UUIDs.CALIBRATION_VERSION_CHARACTERISTIC), Mockito.any(Duration.class)))
                .thenReturn(calibrationVersionCharacteristic);
        BluetoothDevice timeflip = Mockito.mock(BluetoothDevice.class);
        when(timeflip.find(Mockito.eq(UUIDs.TIMEFLIP_SERVICE), Mockito.any(Duration.class)))
                .thenReturn(timeflipService);

        // create a notification registerer for the mocked TimeFlip device
        NotificationRegisterer notificationRegisterer = new NotificationRegisterer(timeflip);

        // actual tests
        Assert.assertNotNull(notificationRegisterer);
        notificationRegisterer.registerFacetsNotification("TIMEFLIP_MAC_ADDRESS", "BACKEND_URL");

    }

    @Test
    public void registerFacetsNotificationNoServiceTest() throws NotificationRegisteringException {

        // mock TimeFlip device
        BluetoothDevice timeflip = Mockito.mock(BluetoothDevice.class);
        when(timeflip.find(Mockito.eq(UUIDs.TIMEFLIP_SERVICE), Mockito.any(Duration.class)))
                .thenReturn(null);

        // create a notification registerer for the mocked TimeFlip device
        NotificationRegisterer notificationRegisterer = new NotificationRegisterer(timeflip);

        // actual tests
        Assert.assertNotNull(notificationRegisterer);
        Assert.assertThrows(
                NotificationRegisteringException.class,
                () -> notificationRegisterer.registerFacetsNotification("TIMEFLIP_MAC_ADDRESS", "BACKEND_URL")
        );

    }

    @Test
    public void registerFacetsNotificationNoCharacteristicsTest() throws NotificationRegisteringException {

        // mock TimeFlip device
        BluetoothGattService timeflipService = Mockito.mock(BluetoothGattService.class);
        when(timeflipService.find(Mockito.eq(UUIDs.PASSWORD_CHARACTERISTIC), Mockito.any(Duration.class)))
                .thenReturn(null);
        when(timeflipService.find(Mockito.eq(UUIDs.FACETS_CHARACTERISTIC), Mockito.any(Duration.class)))
                .thenReturn(null);
        when(timeflipService.find(Mockito.eq(UUIDs.CALIBRATION_VERSION_CHARACTERISTIC), Mockito.any(Duration.class)))
                .thenReturn(null);
        BluetoothDevice timeflip = Mockito.mock(BluetoothDevice.class);
        when(timeflip.find(Mockito.eq(UUIDs.TIMEFLIP_SERVICE), Mockito.any(Duration.class)))
                .thenReturn(timeflipService);

        // create a notification registerer for the mocked TimeFlip device
        NotificationRegisterer notificationRegisterer = new NotificationRegisterer(timeflip);

        // actual tests
        Assert.assertNotNull(notificationRegisterer);
        Assert.assertThrows(
                NotificationRegisteringException.class,
                () -> notificationRegisterer.registerFacetsNotification("TIMEFLIP_MAC_ADDRESS", "BACKEND_URL")
        );

    }
    
    @Test
    public void registerFacetsNotificationPasswordTest() throws NotificationRegisteringException {

        // mock TimeFlip device
        BluetoothGattCharacteristic passwordCharacteristic = Mockito.mock(BluetoothGattCharacteristic.class);
        when(passwordCharacteristic.writeValue(PASSWORD))
                .thenReturn(false);
        BluetoothGattCharacteristic facetsCharacteristic = Mockito.mock(BluetoothGattCharacteristic.class);
        BluetoothGattCharacteristic calibrationVersionCharacteristic = Mockito.mock(BluetoothGattCharacteristic.class);
        BluetoothGattService timeflipService = Mockito.mock(BluetoothGattService.class);
        when(timeflipService.find(Mockito.eq(UUIDs.PASSWORD_CHARACTERISTIC), Mockito.any(Duration.class)))
                .thenReturn(passwordCharacteristic);
        when(timeflipService.find(Mockito.eq(UUIDs.FACETS_CHARACTERISTIC), Mockito.any(Duration.class)))
                .thenReturn(facetsCharacteristic);
        when(timeflipService.find(Mockito.eq(UUIDs.CALIBRATION_VERSION_CHARACTERISTIC), Mockito.any(Duration.class)))
                .thenReturn(calibrationVersionCharacteristic);
        BluetoothDevice timeflip = Mockito.mock(BluetoothDevice.class);
        when(timeflip.find(Mockito.eq(UUIDs.TIMEFLIP_SERVICE), Mockito.any(Duration.class)))
                .thenReturn(timeflipService);

        // create a notification registerer for the mocked TimeFlip device
        NotificationRegisterer notificationRegisterer = new NotificationRegisterer(timeflip);

        // actual tests
        Assert.assertNotNull(notificationRegisterer);
        Assert.assertThrows(
                NotificationRegisteringException.class,
                () -> notificationRegisterer.registerFacetsNotification("TIMEFLIP_MAC_ADDRESS", "BACKEND_URL")
        );

    }

}
