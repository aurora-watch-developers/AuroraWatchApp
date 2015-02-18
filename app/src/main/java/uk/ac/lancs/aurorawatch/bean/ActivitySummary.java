package uk.ac.lancs.aurorawatch.bean;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Arrays;

import uk.ac.lancs.aurorawatch.exception.InvalidActivityTxtException;

/**
 * Class holding the contents of activity.txt
 */
public class ActivitySummary {

    private String creationTime;
    private String station;
    private String statusNumber;
    private String statusText;

    ActivitySummary() {
    }

    ActivitySummary(String creationTime, String station, String statusNumber, String statusText) {
        this.creationTime = creationTime;
        this.station = station;
        this.statusNumber = statusNumber;
        this.statusText = statusText;
    }

    public String getCreationTime() {
        return creationTime;
    }

    void setCreationTime(String creationTime) {
        this.creationTime = creationTime;
    }

    public String getStation() {
        return station;
    }

    void setStation(String station) {
        this.station = station;
    }

    public String getStatusNumber() {
        return statusNumber;
    }

    void setStatusNumber(String statusNumber) {
        this.statusNumber = statusNumber;
    }

    public String getStatusText() {
        return statusText;
    }

    void setStatusText(String statusText) {
        this.statusText = statusText;
    }
}
