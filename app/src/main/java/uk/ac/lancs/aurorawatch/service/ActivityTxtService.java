package uk.ac.lancs.aurorawatch.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.io.File;
import java.io.IOException;

/**
 * An {@link IntentService} subclass for loading the activity.txt from
 * AuroraWatch UK.
 */
public class ActivityTxtService extends IntentService {

    public static final String FETCH_ACTIVITY_TXT = "uk.ac.lancs.aurorawatch.service.action.FETCH_ACTIVITY_TXT";
    public static final String CACHE_FILE = "uk.ac.lancs.aurorawatch.service.extra.CACHE_DIR";

    private static final String ACTIVITY_TXT_URL = "http://aurorawatch.lancs.ac.uk/api/0.1/activity.txt";

    /**
     * Starts this service to perform action FETCH_ACTIVITY_TXT. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    public static void startFetchActivityTxt(Context context, String cacheFile) {
        Intent intent = new Intent(context, ActivityTxtService.class);
        intent.setAction(FETCH_ACTIVITY_TXT);


        intent.putExtra(CACHE_FILE, cacheFile);
        context.startService(intent);
    }

    public ActivityTxtService() {
        super("ActivityTxtService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (FETCH_ACTIVITY_TXT.equals(action)) {
                handleActionFetchActivityTxt(intent);
            }
        }
    }

    /**
     * Handle action FetchActivityTxt in the provided background thread.
     */
    private void handleActionFetchActivityTxt(Intent intent) {
        long currentTime = System.currentTimeMillis();
        long fiveMinutesAgo = currentTime - 5 * 60 * 1000;
        File cacheFile = new File(intent.getStringExtra(CACHE_FILE));
        long lastModified = cacheFile.exists() ? cacheFile.lastModified() : 0;

        if (lastModified > fiveMinutesAgo) {
            Log.d(ActivityTxtService.class.getSimpleName(), "Cache exists: last modified "
                    + cacheFile.lastModified() + ": now " + currentTime);
            return;
        }

        HttpUtil.downloadFile(ACTIVITY_TXT_URL, cacheFile.getAbsolutePath());
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }
}
