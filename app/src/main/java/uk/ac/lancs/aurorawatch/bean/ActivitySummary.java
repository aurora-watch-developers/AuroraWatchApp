package uk.ac.lancs.aurorawatch.bean;

import java.io.BufferedReader;
import java.io.IOException;

/**
 * Class holding the contents of activity.txt
 */
public class ActivitySummary {

    private String station;
    private String creationTime;
    private String statusText;
    private String statusNumber;

    public String getStation() { return station; }

    public void setStation(String station) {
        this.station = station;
    }

    public String getCreationTime() { return creationTime; }

    public void setCreationTime(String creationTime) {
        this.creationTime = creationTime;
    }

    public String getStatusText() {
        return statusText;
    }

    public void setStatusText(String statusText) {
        this.statusText = statusText;
    }

    public String getStatusNumber() {
        return statusNumber;
    }

    public void setStatusNumber(String statusNumber) {
        this.statusNumber = statusNumber;
    }

    public boolean isValid() {
        return station != null && station.trim().length() > 0
                && creationTime != null && creationTime.trim().length() > 0
                && statusText != null && statusText.trim().length() > 0
                && statusNumber != null && statusNumber.trim().length() > 0;
    }

    public static ActivitySummary parse(BufferedReader br) throws IOException {
        ActivitySummary summary = new ActivitySummary();
        String line;
        //TODO we will need to save all statuses for the Hourly view
        String lastStatus = null;

        while ((line = br.readLine()) != null) {
            String[] parts = line.split("\\s", 2);
            switch (parts[0]) {
                case "STATION":
                    summary.setStation(parts[1]);
                    break;
                case "CREATION_TIME":
                    summary.setCreationTime(parts[1]);
                    break;
                case "ACTIVITY":
                    lastStatus = parts[1];
                    break;
            }
        }

        if (lastStatus != null) {
            String[] statusParts = lastStatus.split("\\s");
            if (statusParts.length == 3) {
                summary.setStatusNumber(statusParts[1]);
                summary.setStatusText(statusParts[2]);
            }
        }

        return summary;
    }
}
