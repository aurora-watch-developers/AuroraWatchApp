package uk.ac.lancs.aurorawatch.bean;

import android.content.res.Resources;
import android.util.Log;

import java.util.Properties;

import uk.ac.lancs.aurorawatch.R;

/**
 * Viewable version of activity.txt, formatted for display.
 */
public class ViewableActivitySummary {

    private Properties props;
    private Resources res;
    private ActivitySummary raw;

    private String alertLevelSummary;
    private String alertLevelDetail;
    private Integer color;
    private String disturbanceLevelValue;
    private String disturbanceLevelUnit;
    private String retrievalInfo;


    public ViewableActivitySummary(Properties props, Resources res, ActivitySummary raw) {
        this.props = props;
        this.res = res;
        this.raw = raw;
    }

    public String getAlertLevelSummary() {
        if (alertLevelSummary == null) {
            String PROP_STATUS_SUMMARY = "status.summary.";
            alertLevelSummary = props.getProperty(PROP_STATUS_SUMMARY + raw.getStatusText());
        }
        return alertLevelSummary;
    }

    public String getAlertLevelDetail() {
        if (alertLevelDetail == null) {
            String PROP_STATUS_DETAIL = "status.detail.";
            alertLevelDetail = props.getProperty(PROP_STATUS_DETAIL + raw.getStatusText());
        }
        return alertLevelDetail;
    }

    public Integer getColor() {
        if (color == null) {
            String PROP_STATUS_COLOR = "status.color.";
            String colorString = props.getProperty(PROP_STATUS_COLOR + raw.getStatusText());
            try {
                color = (int) Long.parseLong(colorString, 16);
            } catch (NumberFormatException e) {
                Log.w(ViewableActivitySummary.class.getSimpleName(), "Invalid color", e);
                color = 0xff33ff33;
            }
        }
        return color;
    }

    private void updateDisturbanceLevel() {
        try {
            Float.parseFloat(raw.getStatusNumber());
            disturbanceLevelValue = raw.getStatusNumber();
            disturbanceLevelUnit = res.getString(R.string.disturbanceLevelUnit);
        } catch (NumberFormatException e) {
            Log.w(ViewableActivitySummary.class.getSimpleName(), "Invalid status number", e);
            disturbanceLevelValue = res.getString(R.string.disturbanceLevelUnknown);
            disturbanceLevelUnit = "";
        }
    }

    public String getDisturbanceLevelValue() {
        if (disturbanceLevelValue == null || disturbanceLevelUnit == null) {
            updateDisturbanceLevel();
        }
        return disturbanceLevelValue;
    }

    public String getDisturbanceLevelUnit() {
        if (disturbanceLevelValue == null || disturbanceLevelUnit == null) {
            updateDisturbanceLevel();
        }
        return disturbanceLevelUnit;
    }

    public String getRetrievalInfo() {
        if (retrievalInfo == null) {
            String PROP_STATION = "station.";
            String station = props.getProperty(PROP_STATION + raw.getStation(), raw.getStation());
            retrievalInfo = String.format(res.getString(R.string.retrievalTime),
                station, raw.getCreationTime());
        }
        return retrievalInfo;
    }
}
