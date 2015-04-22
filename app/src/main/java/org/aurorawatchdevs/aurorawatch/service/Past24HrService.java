package org.aurorawatchdevs.aurorawatch.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.io.File;

import org.aurorawatchdevs.aurorawatch.util.HttpUtil;

/**
 * An {@link IntentService} subclass for loading the image of the last 24 hrs activity from
 * AuroraWatch UK.
 */
public class Past24HrService extends IntentService {

    public static final String FETCH_24HR_IMAGE = "org.aurorawatchdevs.aurorawatch.service.action.FETCH_24HR_IMAGE";
    public static final String CACHE_FILE = "org.aurorawatchdevs.aurorawatch.service.extra.CACHE_DIR";

    private static final String PAST24HR_IMG_URL = "http://aurorawatch.lancs.ac.uk/summary/aurorawatch_new/plots/awn_lan1/rolling.png";

    /**
     * Starts this service to perform action FETCH_24HR_IMAGE. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    public static void startFetch24HrImage(Context context, String cacheFile) {
        Intent intent = new Intent(context, Past24HrService.class);
        intent.setAction(FETCH_24HR_IMAGE );


        intent.putExtra(CACHE_FILE, cacheFile);
        context.startService(intent);
    }

    public Past24HrService() {
        super("Past24HrService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (FETCH_24HR_IMAGE.equals(action)) {
                handleActionFetch24HrImage(intent);
            }
        }
    }

    /**
     * Handle action Fetch24HrImage in the provided background thread.
     */
    private void handleActionFetch24HrImage(Intent intent) {
        long currentTime = System.currentTimeMillis();
        long fiveMinutesAgo = currentTime - 5 * 60 * 1000;
        File cacheFile = new File(intent.getStringExtra(CACHE_FILE));
        long lastModified = cacheFile.exists() ? cacheFile.lastModified() : 0;

        if (lastModified > fiveMinutesAgo) {
            Log.d(Past24HrService.class.getSimpleName(), "Cache exists: last modified "
                    + cacheFile.lastModified() + ": now " + currentTime);
            return;
        }

        HttpUtil.downloadFile(PAST24HR_IMG_URL, cacheFile.getAbsolutePath(), getApplicationContext());
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }
}
