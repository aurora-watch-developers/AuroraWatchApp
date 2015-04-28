package org.aurorawatchdevs.aurorawatch.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
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

import org.aurorawatchdevs.aurorawatch.R;
import org.aurorawatchdevs.aurorawatch.bean.ActivitySummary;
import org.aurorawatchdevs.aurorawatch.bean.ViewableActivitySummary;
import org.aurorawatchdevs.aurorawatch.service.ActivityTxtService;
import org.aurorawatchdevs.aurorawatch.util.FileUtil;

/**
 * Fragment showing the "now" view with current disturbance level,
 * alert status and time retrieved.
 */
public class NowFragment extends Fragment {

    private BroadcastReceiver fetchActivityTxtReceiver;
    private BroadcastReceiver parseActivityTxtReceiver;
    private String cacheFile;
    private Properties props;

    private View nowView;
    private TextView alertLevelSummary;
    private TextView alertLevelDetail;
    private TextView disturbanceLevelValue;
    private TextView disturbanceLevelUnit;
    private TextView retrievalTime;

    // Time, in milliseconds, when the activity.txt was last parsed successfully.
    private long lastParseSuccess = 0;

    // Maximum age, in seconds, for the cache file. If the cache file is older than this, an
    // error screen will be shown if it cannot be fetched or parsed.
    private static final int MAX_CACHE_AGE_SEC = 15 * 60;

    /**
     * Lazy initialisation for the cache path.
     */
    private void initCacheFile() {
        if (cacheFile == null) {
            File cacheDir = getActivity().getCacheDir();
            cacheFile = new File(cacheDir, "activity.txt").getAbsolutePath();
        }
    }

    /**
     * Lazy initialisation for the fragment's properties.
     */
    private void loadProperties() {
        if (props == null) {
            props = new Properties();
            try {
                props.load(getActivity().getBaseContext().getAssets().open("now.properties"));

            } catch (IOException e) {
                Log.e(ActivityTxtService.class.getSimpleName(), "Error reading now.properties", e);
            }
        }
    }

    /**
     * Lazy initialisation for the main "now" view and its children.
     */
    private void initNowView() {
        if (nowView == null) {
            nowView = createView(R.layout.now);
            alertLevelSummary = (TextView) nowView.findViewById(R.id.alertLevelSummary);
            alertLevelDetail = (TextView) nowView.findViewById(R.id.alertLevelDetail);
            disturbanceLevelValue = (TextView) nowView.findViewById(R.id.disturbanceLevelValue);
            disturbanceLevelUnit = (TextView) nowView.findViewById(R.id.disturbanceLevelUnit);
            retrievalTime = (TextView) nowView.findViewById(R.id.retrievalTime);
        }
    }

    /**
     * Inflate the view with the given ID.
     * @param viewID the view to inflate
     * @return the inflated view
     */
    private View createView(int viewID) {
        return getLayoutInflater(null).inflate(viewID, (ViewGroup) getView(), false);
    }

    /**
     * Remove the current view and show the given view instead. If the old and new views are the
     * same, do nothing.
     * @param newView the view to show
     */
    private void showView(View newView) {
        ViewGroup parent = (ViewGroup) getView();
        if (parent == null) {
            return;
        }
        if (parent.findViewById(newView.getId()) != null) {
            Log.i(NowFragment.class.getSimpleName(), "New view ID already shown: doing nothing");
            return;
        }
        parent.removeAllViews();
        parent.addView(newView);
    }

    /**
     * Handle a message from the service saying that the activity.txt could not be downloaded.
     * @param cacheFile The file that could not be downloaded
     * @param errorInfo Error message from the service (may be null)
     */
    private void handleMissingActivityTxt(String cacheFile, String errorInfo) {
        if (errorInfo != null) {
            Log.w(NowFragment.class.getSimpleName(), errorInfo);
        }
        if (FileUtil.existsAndIsUpToDate(cacheFile, MAX_CACHE_AGE_SEC)) {
            // something went wrong this time, but we have an old version to show
            Log.d(NowFragment.class.getSimpleName(), "Showing older activity.txt for now");
        } else {
            showView(createView(R.layout.network_error));
            View view = getView();
            if (view != null) {
                View settingsButton = getView().findViewById(R.id.btnOpenSettings);
                if (settingsButton != null) {
                    settingsButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            startActivity(new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS));
                        }
                    });
                }
            }
        }
    }

    /**
     * Handle a message from the service saying that the activity.txt could not be parsed.
     * @param cacheFile The file that could not be parsed
     * @param errorInfo Error message from the service (may be null)
     */
    private void handleInvalidActivityTxt(String cacheFile, String errorInfo) {
        if (errorInfo != null) {
            Log.w(NowFragment.class.getSimpleName(), errorInfo);
        }
        if (!FileUtil.existsAndIsUpToDate(cacheFile, MAX_CACHE_AGE_SEC)) {
            showView(createView(R.layout.network_error));
        }
        long currentTime = System.currentTimeMillis();
        if (lastParseSuccess + MAX_CACHE_AGE_SEC * 1000 > currentTime) {
            // something went wrong this time, but we have an old version to show
            Log.d(NowFragment.class.getSimpleName(), "Showing older activity.txt for now");
        } else {
            showView(createView(R.layout.format_error));
            View view = getView();
            if (view != null) {
                View settingsButton = getView().findViewById(R.id.btnCheckForUpdates);
                if (settingsButton != null) {
                    settingsButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            intent.setData(Uri.parse(getString(R.string.marketURL)));
                            startActivity(intent);
                        }
                    });
                }
            }
        }

    }

    /**
     * Called when the activity.txt has been successfully downloaded and parsed, to update the UI
     * with the information it contains.
     * @param as The parsed activity.txt
     */
    private void updateUIFromActivity(ActivitySummary as) {
        initNowView();
        loadProperties();
        ViewableActivitySummary vs = new ViewableActivitySummary(props, getResources(), as);

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

        showView(nowView);
    }

    // Lifecycle methods
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if (savedInstanceState != null) {
            lastParseSuccess = savedInstanceState.getLong("lastParseSuccess", 0);
        }

        int layout;
        initCacheFile();
        if (FileUtil.existsAndIsUpToDate(cacheFile, MAX_CACHE_AGE_SEC)) {
            layout = R.layout.now;
        } else {
            layout = R.layout.loading;
        }
        return inflater.inflate(layout, container, false);
    }

    @Override
    public void onActivityCreated(final Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Broadcast receiver for activity.txt download
        fetchActivityTxtReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d(NowFragment.class.getSimpleName(), "receive " + ActivityTxtService.FETCH_ACTIVITY_TXT);
                String errorInfo = intent.getStringExtra(ActivityTxtService.ERROR_INFO);
                boolean fileNotFound = intent.getBooleanExtra(ActivityTxtService.FILE_NOT_FOUND, false);
                String cacheFile = intent.getStringExtra(ActivityTxtService.CACHE_FILE);

                if (fileNotFound || errorInfo != null) {
                    handleMissingActivityTxt(cacheFile, errorInfo);
                } else {
                    ActivityTxtService.startParseActivityTxt(getActivity(), cacheFile);
                }
            }
        };

        // Broadcast receiver for activity.txt parse
        parseActivityTxtReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d(NowFragment.class.getSimpleName(), "receive " + ActivityTxtService.PARSE_ACTIVITY_TXT);
                String errorInfo = intent.getStringExtra(ActivityTxtService.ERROR_INFO);
                boolean fileNotFound = intent.getBooleanExtra(ActivityTxtService.FILE_NOT_FOUND, false);
                boolean parseError = intent.getBooleanExtra(ActivityTxtService.PARSE_ERROR, false);
                String cacheFile = intent.getStringExtra(ActivityTxtService.CACHE_FILE);
                ActivitySummary summary = intent.getParcelableExtra(ActivityTxtService.PARSED_ACTIVITY);

                if (fileNotFound || parseError || errorInfo != null) {
                    handleInvalidActivityTxt(cacheFile, errorInfo);
                } else {
                    updateUIFromActivity(summary);
                }
            }
        };

        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(
                fetchActivityTxtReceiver,
                new IntentFilter(ActivityTxtService.FETCH_ACTIVITY_TXT));

        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(
                parseActivityTxtReceiver,
                new IntentFilter(ActivityTxtService.PARSE_ACTIVITY_TXT));

        initCacheFile();
        ActivityTxtService.startFetchActivityTxt(getActivity(), cacheFile);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putLong("lastParseSuccess", lastParseSuccess);
    }

    @Override
    public void onDestroy() {
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(fetchActivityTxtReceiver);
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(parseActivityTxtReceiver);
        super.onDestroy();
    }

    public static NowFragment newInstance() {
        return new NowFragment();
    }
}
