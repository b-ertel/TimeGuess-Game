package at.timeguess.raspberry;

/**
 * The UUIDs of the TimeFlip device's services and characteristics.
 */
public class UUIDs {

    // UUIDs Device Information Service
    public static final String DEVICE_INFORMATION_SERVICE = "0000180a-0000-1000-8000-00805f9b34fb";
    public static final String FIRMWARE_REVISION_STRING_CHARACTERISTIC = "00002a26-0000-1000-8000-00805f9b34fb";

    // UUIDs Battery Service
    public static final String BATTERY_SERVICE = "0000180f-0000-1000-8000-00805f9b34fb";
    public static final String BATTERY_LEVEL_CHARACTERISTIC = "00002a19-0000-1000-8000-00805f9b34fb";

    // UUIDs TimeFlip service
    public static final String TIMEFLIP_SERVICE = "f1196f50-71a4-11e6-bdf4-0800200c9a66";
    public static final String ACCELEROMETER_DATA_CHARACTERISTIC = "f1196f51-71a4-11e6-bdf4-0800200c9a66";
    public static final String FACETS_CHARACTERISTIC = "f1196f52-71a4-11e6-bdf4-0800200c9a66";
    public static final String COMMAND_RESULT_OUTPUT_CHARACTERISTIC = "f1196f53-71a4-11e6-bdf4-0800200c9a66";
    public static final String COMMAND_CHARACTERISTIC = "f1196f54-71a4-11e6-bdf4-0800200c9a66";
    public static final String DOUBLE_TAP_DEFINITION_CHARACTERISTIC = "f1196f55-71a4-11e6-bdf4-0800200c9a66";
    public static final String CALIBRATION_VERSION_CHARACTERISTIC = "f1196f56-71a4-11e6-bdf4-0800200c9a66";
    public static final String PASSWORD_CHARACTERISTIC = "f1196f57-71a4-11e6-bdf4-0800200c9a66";

}
