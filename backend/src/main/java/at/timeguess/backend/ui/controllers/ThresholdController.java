package at.timeguess.backend.ui.controllers;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import at.timeguess.backend.model.ThresholdType;
import at.timeguess.backend.services.CubeService;
import at.timeguess.backend.ui.beans.MessageBean;

/**
 * Controller for the cube health status view.
 */
@Component
@Scope("view")
public class ThresholdController {

    @Autowired
    private CubeService cubeService;

    @Autowired
    private MessageBean messageBean;

    private int batteryLevelThreshold;
    private int rssiThreshold;

    @PostConstruct
    public void init() {
        batteryLevelThreshold = getSavedBatteryLevelThreshold();
        rssiThreshold = getSavedRssiThreshold();
    }

    public int getBatteryLevelThreshold() {
        return batteryLevelThreshold;
    }

    public void setBatteryLevelThreshold(int batteryLevelThreshold) {
        this.batteryLevelThreshold = batteryLevelThreshold;
    }

    public int getRssiThreshold() {
        return rssiThreshold;
    }

    public void setRssiThreshold(int rssiThreshold) {
        this.rssiThreshold = rssiThreshold;
    }

    /**
     * Get the battery level threshold from the database.
     * @return the battery level threshold
     */
    public int getSavedBatteryLevelThreshold() {
        return cubeService.queryThreshold(ThresholdType.BATTERY_LEVEL_THRESHOLD);
    }

    /**
     * Get the RSSI threshold from the database.
     * @return the RSSI threshold
     */
    public int getSavedRssiThreshold() {
        return cubeService.queryThreshold(ThresholdType.RSSI_THRESHOLD);
    }

    /**
     * Update the battery level threshold.
     */
    public void doUpdateBatteryLevelThreshold() {
        cubeService.updateThreshold(ThresholdType.BATTERY_LEVEL_THRESHOLD, batteryLevelThreshold);
        messageBean.alertInformation("Thresholds", "Battery level threshold successfully updated.");
    }

    /**
     * Update the RSSI threshold.
     */
    public void doUpdateRssiThreshold() {
        cubeService.updateThreshold(ThresholdType.RSSI_THRESHOLD, rssiThreshold);
        messageBean.alertInformation("Thresholds", "RSSI threshold successfully updated.");
    }

}
