package at.timeguess.raspberry;

import org.junit.Assert;
import org.junit.Test;

import tinyb.BluetoothDevice;
import tinyb.BluetoothGattCharacteristic;
import tinyb.BluetoothGattService;

import org.mockito.Mockito;

import static org.mockito.Mockito.when;

import java.time.Duration;

/**
 * Some tests for the {@link TimeFlipWrapper} class.
 */
public class TimeFlipWrapperTest {

    /**
     * Create a mocked TimeFlip device.
     * 
     * @return a mocked TimeFlip device
     */
    private BluetoothDevice getMockedTimeFlip() {
        BluetoothDevice timeFlip = Mockito.mock(BluetoothDevice.class);

        BluetoothGattService batteryService = Mockito.mock(BluetoothGattService.class);
        BluetoothGattService timeflipService = Mockito.mock(BluetoothGattService.class);

        BluetoothGattCharacteristic batteryLevelCharacteristic = Mockito.mock(BluetoothGattCharacteristic.class);
        BluetoothGattCharacteristic facetsCharacteristic = Mockito.mock(BluetoothGattCharacteristic.class);
        BluetoothGattCharacteristic calibrationVersionCharacteristic = Mockito.mock(BluetoothGattCharacteristic.class);
        BluetoothGattCharacteristic passwordCharacteristic = Mockito.mock(BluetoothGattCharacteristic.class);

        byte[] one = {0x1, 0x0, 0x0, 0x0};

        when(calibrationVersionCharacteristic.readValue())
                .thenReturn(one);
        when(calibrationVersionCharacteristic.writeValue(one))
                .thenReturn(true);

        when(passwordCharacteristic.writeValue(TimeFlipWrapper.PASSWORD))
                .thenReturn(true);

        when(batteryService.find(Mockito.eq(TimeFlipWrapper.BATTERY_LEVEL_CHARACTERISTIC), Mockito.any(Duration.class)))
                .thenReturn(batteryLevelCharacteristic);
        when(timeflipService.find(Mockito.eq(TimeFlipWrapper.FACETS_CHARACTERISTIC), Mockito.any(Duration.class)))
                .thenReturn(facetsCharacteristic);
        when(timeflipService.find(Mockito.eq(TimeFlipWrapper.CALIBRATION_VERSION_CHARACTERISTIC), Mockito.any(Duration.class)))
                .thenReturn(calibrationVersionCharacteristic);
        when(timeflipService.find(Mockito.eq(TimeFlipWrapper.PASSWORD_CHARACTERISTIC), Mockito.any(Duration.class)))
                .thenReturn(passwordCharacteristic);

        when(timeFlip.find(Mockito.eq(TimeFlipWrapper.BATTERY_SERVICE), Mockito.any(Duration.class)))
                .thenReturn(batteryService);
        when(timeFlip.find(Mockito.eq(TimeFlipWrapper.TIMEFLIP_SERVICE), Mockito.any(Duration.class)))
                .thenReturn(timeflipService);

        when(timeFlip.connect())
                .thenReturn(true);

        return timeFlip;
    }

    @Test
    public void readCalibrationVersionTest() {
        TimeFlipWrapper timeFlipWrapper = new TimeFlipWrapper(getMockedTimeFlip());
        Assert.assertNotNull(timeFlipWrapper);
        Assert.assertTrue(timeFlipWrapper.connect());
        Assert.assertTrue(timeFlipWrapper.findServicesAndCharacteristics());
        Assert.assertTrue(timeFlipWrapper.writePassword());
        Assert.assertEquals(1, timeFlipWrapper.readCalibrationVersion());
    }

    @Test
    public void writeCalibrationVersionTest() {
        TimeFlipWrapper timeFlipWrapper = new TimeFlipWrapper(getMockedTimeFlip());
        Assert.assertNotNull(timeFlipWrapper);
        Assert.assertTrue(timeFlipWrapper.connect());
        Assert.assertTrue(timeFlipWrapper.findServicesAndCharacteristics());
        Assert.assertTrue(timeFlipWrapper.writePassword());
        Assert.assertTrue(timeFlipWrapper.writeCalibrationVersion(1));
    }

}

