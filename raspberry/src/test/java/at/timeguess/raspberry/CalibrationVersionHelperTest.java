package at.timeguess.raspberry;

import org.junit.Assert;
import org.junit.Test;

import tinyb.BluetoothGattCharacteristic;

import org.mockito.Mockito;

import static org.mockito.Mockito.when;

public class CalibrationVersionHelperTest {

    @Test
    public void readCalibrationVersionTest() {

        // mock calibration version characteristic
        BluetoothGattCharacteristic calibrationVersionCharacteristic = Mockito.mock(BluetoothGattCharacteristic.class);
        byte[] one = {0x1, 0x0, 0x0, 0x0};
        when(calibrationVersionCharacteristic.readValue()).thenReturn(one);

        // create a calibration Version helper for the mocked characteristic
        CalibrationVersionHelper calibrationVersionHelper = new CalibrationVersionHelper(calibrationVersionCharacteristic);

        // actual tests
        Assert.assertNotNull(calibrationVersionHelper);
        Assert.assertEquals(1, calibrationVersionHelper.readCalibrationVersion());

    }

    @Test
    public void writeCalibrationVersionTest() {

        // mock calibration version characteristic
        BluetoothGattCharacteristic calibrationVersionCharacteristic = Mockito.mock(BluetoothGattCharacteristic.class);
        byte[] one = {0x1, 0x0, 0x0, 0x0};
        when(calibrationVersionCharacteristic.writeValue(one)).thenReturn(true);

        // create a calibration Version helper for the mocked characteristic
        CalibrationVersionHelper calibrationVersionHelper = new CalibrationVersionHelper(calibrationVersionCharacteristic);

        // actual tests
        Assert.assertNotNull(calibrationVersionHelper);
        Assert.assertTrue(calibrationVersionHelper.writeCalibrationVersion(1));

    }

}
