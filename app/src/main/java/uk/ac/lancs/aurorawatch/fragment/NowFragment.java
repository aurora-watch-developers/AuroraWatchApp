package uk.ac.lancs.aurorawatch.fragment;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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

    private TextView disturbanceLevelValue;
    private TextView disturbanceLevelUnit;

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
        if (summary == null) {
            return;
        }
        if (summary.getStatusNumber() != null) {
            disturbanceLevelValue.setText(summary.getStatusNumber());
            disturbanceLevelUnit.setText(getString(R.string.disturbanceLevelUnit));
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        View view = getView();
        if (view != null) {
            disturbanceLevelValue = (TextView) getView().findViewById(R.id.disturbanceLevelValue);
            disturbanceLevelUnit = (TextView) getView().findViewById(R.id.disturbanceLevelUnit);

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
