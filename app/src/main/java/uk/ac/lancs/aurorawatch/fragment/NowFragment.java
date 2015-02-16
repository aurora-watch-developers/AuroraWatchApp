package uk.ac.lancs.aurorawatch.fragment;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Properties;

import uk.ac.lancs.aurorawatch.bean.ActivitySummary;
import uk.ac.lancs.aurorawatch.R;
import uk.ac.lancs.aurorawatch.service.ActivityTxtService;

/**
 * Fragment showing the "now" view with current disturbance level,
 * alert status and time retrieved.
 */
public class NowFragment extends Fragment {

    private TextView alertLevelSummary;
    private TextView alertLevelDetail;
    private TextView disturbanceLevelValue;
    private TextView disturbanceLevelUnit;
    private TextView retrievalTime;

    private BroadcastReceiver receiver;
    private String cacheFile;

    private Properties props;

    private static String PROP_PREFIX = "now.";
    private static String PROP_STATUS_SUMMARY = PROP_PREFIX + "status.summary.";
    private static String PROP_STATUS_DETAIL = PROP_PREFIX + "status.detail.";
    private static String PROP_STATUS_COLOR = PROP_PREFIX + "status.color.";
    private static String PROP_STATION = PROP_PREFIX + "station.";


    private void loadProperties() {
        props = new Properties();
        try {
            //load a properties file
            Activity a = getActivity();
            Context c = a.getBaseContext();
            AssetManager am = c.getAssets();
            am.open("messages.properties");

            props.load(getActivity().getBaseContext().getAssets().open("messages.properties"));

        } catch (IOException e) {
            Log.e(NowFragment.class.getSimpleName(), "Error reading messages.properties", e);
        }
    }

    private ActivitySummary readActivityTxt(String path) {
        File cacheFile = new File(path);
        if (!cacheFile.exists()) {
            return null;
        }
        BufferedReader br = null;
        ActivitySummary summary = null;
        try {
            br = new BufferedReader(new FileReader(cacheFile));
            summary = ActivitySummary.parse(br);
        } catch (IOException e) {
            Log.e(NowFragment.class.getSimpleName(), "Error reading activity.txt", e);
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

    private void updateUI(String cacheFile) {
        ActivitySummary summary = readActivityTxt(cacheFile);
        if (summary == null || !summary.isValid()) {
            Log.w(NowFragment.class.getSimpleName(), "Invalid activity.txt");
            return;
        }

        String status = summary.getStatusText().toLowerCase();
        if (!(Arrays.asList("green", "yellow", "amber", "red")).contains(status)) {
            Log.w(NowFragment.class.getSimpleName(), "Unknown status " + status);
            return;
        }

        // Use the status to select the right text and color
        // Coloured top bar
        String statusSummary = props.getProperty(PROP_STATUS_SUMMARY + status);
        String colorString = props.getProperty(PROP_STATUS_COLOR + status);
        int color = (int)Long.parseLong(colorString, 16);
        alertLevelSummary.setText(statusSummary);
        ((View)alertLevelSummary.getParent()).setBackgroundColor(color);

        // Raw number
        disturbanceLevelValue.setText(summary.getStatusNumber());
        disturbanceLevelValue.setTextColor(color);
        disturbanceLevelUnit.setTextColor(color);

        // Detail message "Aurora is likely to be visible from..."
        String statusDetail = props.getProperty(PROP_STATUS_DETAIL + status);
        alertLevelDetail.setText(statusDetail);

        // Station and timestamp
        Resources res = getResources();
        String station = props.getProperty(PROP_STATION + summary.getStation(), summary.getStation());
        retrievalTime.setText(String.format(res.getString(R.string.retrievalTime),
                station, summary.getCreationTime()));
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        loadProperties();

        View view = getView();
        if (view != null) {
            alertLevelSummary = (TextView) getView().findViewById(R.id.alertLevelSummary);
            alertLevelDetail = (TextView) getView().findViewById(R.id.alertLevelDetail);
            disturbanceLevelValue = (TextView) getView().findViewById(R.id.disturbanceLevelValue);
            disturbanceLevelUnit = (TextView) getView().findViewById(R.id.disturbanceLevelUnit);
            retrievalTime = (TextView) getView().findViewById(R.id.retrievalTime);

            updateUI(cacheFile);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                updateUI(intent.getStringExtra(ActivityTxtService.CACHE_FILE));
            }
        };

        File cacheDir = getActivity().getCacheDir();
        cacheFile = new File(cacheDir, "activity.txt").getAbsolutePath();

        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(
                receiver,
                new IntentFilter(ActivityTxtService.FETCH_ACTIVITY_TXT));

        ActivityTxtService.startFetchActivityTxt(getActivity(), cacheFile);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.now, container, false);
    }

    @Override
    public void onDestroy() {
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(receiver);
        super.onDestroy();
    }

    public static NowFragment newInstance() {
        return new NowFragment();
    }
}
