package at.timeguess.backend.ui.controllers;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import at.timeguess.backend.model.IntervalType;
import at.timeguess.backend.services.CubeService;
import at.timeguess.backend.ui.beans.MessageBean;

@Component
@Scope("view")
public class IntervalController {

    @Autowired
    private CubeService cubeService;

    @Autowired
    private MessageBean messageBean;

    private int reportingInterval;
    private int expirationInterval;

    @PostConstruct
    public void init() {
        reportingInterval = getSavedReportingInterval();
        expirationInterval = getSavedExpirationInterval();
    }

    public int getReportingInterval() {
        return reportingInterval;
    }

    public void setReportingInterval(int reportingInterval) {
        this.reportingInterval = reportingInterval;
    }

    public int getExpirationInterval() {
        return expirationInterval;
    }

    public void setExpirationInterval(int expirationInterval) {
        this.expirationInterval = expirationInterval;
    }

    /**
     * Get the reporting interval from the database.
     * 
     * @return the reporting interval
     */
    public int getSavedReportingInterval() {
        return cubeService.queryInterval(IntervalType.REPORTING_INTERVAL);
    }

    /**
     * Get the expiration interval from the database.
     * 
     * @return the expiration interval
     */
    public int getSavedExpirationInterval() {
        return cubeService.queryInterval(IntervalType.EXPIRATION_INTERVAL);
    }

    /**
     * Update the reporting interval.
     */
    public void doUpdateReportingInterval() {
        cubeService.updateInterval(IntervalType.REPORTING_INTERVAL, reportingInterval);
        messageBean.alertInformation("Intervals", "Reporting interval successfully updated.");
    }

    /**
     * Update the expiration interval.
     */
    public void doUpdateExpirationInterval() {
        cubeService.updateInterval(IntervalType.EXPIRATION_INTERVAL, expirationInterval);
        messageBean.alertInformation("Intervals", "Expiration interval successfully updated.");
    }

}

