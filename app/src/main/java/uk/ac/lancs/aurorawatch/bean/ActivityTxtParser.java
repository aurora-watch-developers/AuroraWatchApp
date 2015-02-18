package uk.ac.lancs.aurorawatch.bean;

import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;

import uk.ac.lancs.aurorawatch.exception.InvalidActivityTxtException;

/**
 * Parse the activity.txt file.
 */
public class ActivityTxtParser {

    private static ActivityTxtParser instance;

    private ActivityTxtParser() {

    }

    public static ActivityTxtParser getInstance() {
        if (instance == null) {
            instance = new ActivityTxtParser();
        }
        return instance;
    }

    public ActivityTxtParser validateNotEmpty(String name, String value) {
        if (value == null || value.trim().length() == 0) {
            throw new InvalidActivityTxtException("Invalid " + name + ": cannot be blank");
        }
        return this;
    }

    public ActivityTxtParser validateKnownStatus(String status) {
        if (!(Arrays.asList("green", "yellow", "amber", "red")).contains(status)) {
            throw new InvalidActivityTxtException("Unknown status: " + status);
        }
        return this;
    }

    public void validate(ActivitySummary summary) {
        validateNotEmpty("creationTime", summary.getCreationTime());
        validateNotEmpty("station", summary.getStation());
        validateNotEmpty("statusNumber", summary.getStatusNumber());
        validateNotEmpty("statusText", summary.getStatusText()).validateKnownStatus(summary.getStatusText());
    }

    public ActivitySummary parse(BufferedReader br) throws InvalidActivityTxtException {

        //TODO we will need to save all statuses for the Hourly view
        ActivitySummary summary = new ActivitySummary();
        String lastStatus = null, line;
        try {
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
        } catch (IOException e) {
            throw new InvalidActivityTxtException("Error reading activity.txt", e);
        }

        if (lastStatus != null) {
            String[] statusParts = lastStatus.split("\\s");
            if (statusParts.length == 3) {
                summary.setStatusNumber(statusParts[1]);
                summary.setStatusText(statusParts[2]);
            }
        }

        validate(summary);
        return summary;
    }

    public ActivitySummary parse(String path) throws InvalidActivityTxtException{
        File cacheFile = new File(path);
        if (!cacheFile.exists()) {
            throw new InvalidActivityTxtException("activity.txt not found", new FileNotFoundException());
        }

        BufferedReader br = null;
        ActivitySummary summary = null;
        try {
            br = new BufferedReader(new FileReader(cacheFile));
            summary = parse(br);
        } catch (IOException e) {
            throw new InvalidActivityTxtException("Error opening activity.txt", e);
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException ignore) {

                }
            }
        }

        return summary;
    }
}
