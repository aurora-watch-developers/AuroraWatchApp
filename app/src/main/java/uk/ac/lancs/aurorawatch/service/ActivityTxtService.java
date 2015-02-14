package uk.ac.lancs.aurorawatch.service;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

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

        if (!cacheFile.exists()) {
            try {
                boolean result = cacheFile.createNewFile();
                if (!result) {
                    Log.w(ActivityTxtService.class.getSimpleName(), "Create " + ACTIVITY_TXT_URL + " returned false");
                }
            } catch (IOException e) {
                Log.e(ActivityTxtService.class.getSimpleName(), "Error creating " + ACTIVITY_TXT_URL, e);
            }
        }

        new HttpRequestTask().execute(ACTIVITY_TXT_URL, cacheFile.getAbsolutePath());

        if (cacheFile.exists()) {
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
        }

    }

    //TODO this shares a lot of code with AuroraWatchUK, consider refactoring
    class HttpRequestTask extends AsyncTask<String, Void, Void> {

        protected Void doInBackground(String... params) {

            String urlString = params[0];
            String outFile = params[1];
            HttpURLConnection conn = null;
            InputStream in = null;
            OutputStream out = null;

            try {
                URL url = new URL(urlString);

                Log.i(getClass().getSimpleName(), "Fetching " + url.toString());
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");


                in = new BufferedInputStream(conn.getInputStream());
                out = new FileOutputStream(outFile);

                byte data[] = new byte[1024];
                int count;
                while ((count = in.read(data)) != -1) {
                    out.write(data, 0, count);
                }

                out.flush();
                out.close();
                in.close();

                Log.i(getClass().getSimpleName(), "Fetched " + urlString);

            } catch (IOException e) {
                Log.e(getClass().getSimpleName(), "Could not fetch " + urlString, e);


            } finally {
                if (conn != null) {
                    conn.disconnect();
                }
                if (in != null) {
                    try {
                        in.close();
                    } catch (IOException ignore) {
                    }
                }
                if (out != null) {
                    try {
                        out.close();
                    } catch (IOException ignore) {
                    }
                }
            }
            return null;
        }


    }
}
