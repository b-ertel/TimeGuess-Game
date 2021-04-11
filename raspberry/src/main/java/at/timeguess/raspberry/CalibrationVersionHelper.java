package at.timeguess.raspberry;

import tinyb.BluetoothGattCharacteristic;

/**
 * A wrapper class around the Calibration Version characteristic of the
 * TimeFlip service of the TimeFlip device that provides convenience methods
 * for reading from and writing to the device.
 */
public class CalibrationVersionHelper {

    private final BluetoothGattCharacteristic calibrationVersionCharacteristic;

    public CalibrationVersionHelper(BluetoothGattCharacteristic calibrationVersionCharacteristic) {
        this.calibrationVersionCharacteristic = calibrationVersionCharacteristic;
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
