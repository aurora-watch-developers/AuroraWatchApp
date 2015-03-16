package uk.ac.lancs.aurorawatch.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.io.FileNotFoundException;

import uk.ac.lancs.aurorawatch.bean.ActivityTxtParser;
import uk.ac.lancs.aurorawatch.util.FileUtil;
import uk.ac.lancs.aurorawatch.util.HttpUtil;

/**
 * An {@link IntentService} subclass for loading the activity.txt from
 * AuroraWatch UK.
 */
public class ActivityTxtService extends IntentService {

    public static final String FETCH_ACTIVITY_TXT = "uk.ac.lancs.aurorawatch.service.action.FETCH_ACTIVITY_TXT";
    public static final String PARSE_ACTIVITY_TXT = "uk.ac.lancs.aurorawatch.service.action.PARSE_ACTIVITY_TXT";

    public static final String CACHE_FILE = "uk.ac.lancs.aurorawatch.service.extra.CACHE_DIR";
    public static final String PARSED_ACTIVITY = "uk.ac.lancs.aurorawatch.service.extra.PARSED_ACTIVITY";

    public static final String ERROR_INFO = "uk.ac.lancs.aurorawatch.service.extra.ERROR_INFO";
    public static final String FILE_NOT_FOUND = "uk.ac.lancs.aurorawatch.service.extra.FILE_NOT_FOUND";
    public static final String PARSE_ERROR = "uk.ac.lancs.aurorawatch.service.extra.PARSE_ERROR";

    private static final String ACTIVITY_TXT_URL = "http://aurorawatch.lancs.ac.uk/api/0.1/activity.txt";
    private static final int MIN_DOWNLOAD_INTERVAL_SEC = 5 * 60;

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

    /**
     * Starts this service to perform action FETCH_ACTIVITY_TXT. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    public static void startParseActivityTxt(Context context, String cacheFile) {
        Intent intent = new Intent(context, ActivityTxtService.class);
        intent.setAction(PARSE_ACTIVITY_TXT);
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
            } else if (PARSE_ACTIVITY_TXT.equals(action)) {
                handleActionParseActivityTxt(intent);
            }
        }
    }

    /**
     * Handle action FetchActivityTxt in the provided background thread.
     */
    private void handleActionFetchActivityTxt(Intent intent) {
        String path = intent.getStringExtra(CACHE_FILE);
        if (FileUtil.existsAndIsUpToDate(path, MIN_DOWNLOAD_INTERVAL_SEC)) {
            Log.d(ActivityTxtService.class.getSimpleName(), "Cache exists");
        }
        else {

            try {
                HttpUtil.downloadFile(ACTIVITY_TXT_URL, path);
            } catch (Exception e) {
                intent.putExtra(ERROR_INFO, e.toString());
            }

            if (!FileUtil.existsAndIsUpToDate(path, MIN_DOWNLOAD_INTERVAL_SEC)) {
                intent.putExtra(FILE_NOT_FOUND, true);
            }
        }

        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    /**
     * Handle action ParseActivityTxt in the provided background thread.
     */
    private void handleActionParseActivityTxt(Intent intent) {
        String path = intent.getStringExtra(CACHE_FILE);
        try {
            Parcelable summary = ActivityTxtParser.getInstance().parse(path);
            intent.putExtra(ActivityTxtService.PARSED_ACTIVITY, summary);

        } catch (FileNotFoundException e) {
            intent.putExtra(FILE_NOT_FOUND, true);
            intent.putExtra(ERROR_INFO, e.toString());
        } catch (Exception e) {
            FileUtil.deleteQuietly(path);
            intent.putExtra(PARSE_ERROR, true);
            intent.putExtra(ERROR_INFO, e.toString());
        }

        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }
}
