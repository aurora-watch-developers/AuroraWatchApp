package uk.ac.lancs.aurorawatch.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Class holding the contents of activity.txt
 */
public class ActivitySummary implements Parcelable {

    private String creationTime;
    private String station;
    private String statusNumber;
    private String statusText;

    ActivitySummary() {
    }

    ActivitySummary(Parcel in) {
        new ActivitySummary(in.readString(), in.readString(), in.readString(), in.readString());
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

    // Parcelable methods
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(creationTime);
        out.writeString(station);
        out.writeString(statusNumber);
        out.writeString(statusText);
    }

    public static final Parcelable.Creator<ActivitySummary> CREATOR
            = new Parcelable.Creator<ActivitySummary>() {
        public ActivitySummary createFromParcel(Parcel in) {
            return new ActivitySummary(in);
        }

        public ActivitySummary[] newArray(int size) {
            return new ActivitySummary[size];
        }
    };
}
