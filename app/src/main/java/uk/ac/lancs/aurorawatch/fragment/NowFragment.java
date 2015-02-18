package uk.ac.lancs.aurorawatch.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

import uk.ac.lancs.aurorawatch.R;
import uk.ac.lancs.aurorawatch.bean.ActivitySummary;
import uk.ac.lancs.aurorawatch.bean.ActivityTxtParser;
import uk.ac.lancs.aurorawatch.bean.ViewableActivitySummary;
import uk.ac.lancs.aurorawatch.exception.InvalidActivityTxtException;
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

    private void loadProperties() {
        props = new Properties();
        try {
            props.load(getActivity().getBaseContext().getAssets().open("now.properties"));

        } catch (IOException e) {
            Log.e(NowFragment.class.getSimpleName(), "Error reading now.properties", e);
        }
    }

    private void updateUI(String cacheFile) {

        ActivitySummary summary;
        try {
            summary = ActivityTxtParser.getInstance().parse(cacheFile);

        } catch (InvalidActivityTxtException e) {
            Log.w(NowFragment.class.getSimpleName(), e.toString());
            return;
        }
        ViewableActivitySummary vs = new ViewableActivitySummary(props, getResources(), summary);

        // Coloured top bar
        alertLevelSummary.setText(vs.getAlertLevelSummary());
        ((View)alertLevelSummary.getParent()).setBackgroundColor(vs.getColor());

        // Raw number
        disturbanceLevelValue.setText(vs.getDisturbanceLevelValue());
        disturbanceLevelValue.setTextColor(vs.getColor());
        disturbanceLevelUnit.setText(vs.getDisturbanceLevelUnit());
        disturbanceLevelUnit.setTextColor(vs.getColor());

        // Detail message "Aurora is likely to be visible from..."
        alertLevelDetail.setText(vs.getAlertLevelDetail());

        // Station and timestamp
       retrievalTime.setText(vs.getRetrievalInfo());
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
