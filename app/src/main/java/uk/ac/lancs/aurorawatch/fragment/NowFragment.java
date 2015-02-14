package uk.ac.lancs.aurorawatch.fragment;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import uk.ac.lancs.aurorawatch.bean.ActivitySummary;
import uk.ac.lancs.aurorawatch.R;
import uk.ac.lancs.aurorawatch.service.ActivityTxtService;

/**
 * Fragment showing the "now" view with current disturbance level,
 * alert status and time retrieved.
 */
@SuppressLint("NewApi")
public class NowFragment extends Fragment {

    private TextView alertLevelSummary;
    private TextView alertLevelDetail;
    private TextView disturbanceLevelValue;
    private TextView disturbanceLevelUnit;
    private TextView retrievalTime;

    private BroadcastReceiver receiver;
    private String cacheFile;

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
            return;
        }

        // Use the status to select the right text and color
        Resources res = getResources();
        int color = 0, index = 0; // these defaults are unused due to summary.isValid() check above
        if (summary.getStatusText().equalsIgnoreCase("green")) {
            index = 0;
            color = 0xff33ff33;

        } else if (summary.getStatusText().equalsIgnoreCase("yellow")) {
            index = 1;
            color = 0xffffff00;

        } else if (summary.getStatusText().equalsIgnoreCase("amber")) {
            index = 2;
            color = 0xffff9900;

        } else if (summary.getStatusText().equalsIgnoreCase("red")) {
            index = 3;
            color = 0xffff0000;
        }

        // Coloured top bar
        alertLevelSummary.setText(res.getStringArray(R.array.status_summary)[index]);
        ((View)alertLevelSummary.getParent()).setBackgroundColor(color);

        // Raw number
        disturbanceLevelValue.setText(summary.getStatusNumber());
        disturbanceLevelValue.setTextColor(color);
        disturbanceLevelUnit.setTextColor(color);

        // Detail message "Aurora is likely to be visible from..."
        alertLevelDetail.setText(res.getStringArray(R.array.status_detail)[index]);

        // Station and timestamp
        retrievalTime.setText(String.format(res.getString(R.string.retrievalTime),
                summary.getStation(), summary.getCreationTime()));
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

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
}
