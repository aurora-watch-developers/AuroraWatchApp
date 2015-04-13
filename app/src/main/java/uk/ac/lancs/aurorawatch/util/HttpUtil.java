package uk.ac.lancs.aurorawatch.util;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import uk.ac.lancs.aurorawatch.R;

/**
 * HTTP utilities.
 */
public class HttpUtil {

    public static boolean downloadFile(String urlString, String path, Context context) {
        HttpURLConnection conn = null;
        InputStream in = null;
        OutputStream out = null;


        File file = new File(path);
        try {

            URL url = new URL(urlString);

            Log.i(HttpUtil.class.getSimpleName(), "Fetching " + url.toString());
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestProperty("User-Agent", context.getResources().getString(R.string.appName));
            conn.setRequestMethod("POST");


            in = new BufferedInputStream(conn.getInputStream());
            if (!file.exists()) {
                boolean result = file.createNewFile();
                if (!result) {
                    throw new IOException("Create " + path + " returned false");
                }
            }
            out = new FileOutputStream(file);

            byte data[] = new byte[1024];
            int count;
            while ((count = in.read(data)) != -1) {
                out.write(data, 0, count);
            }

            out.flush();
            out.close();
            in.close();

            Log.i(HttpUtil.class.getSimpleName(), "Fetched " + urlString);
            return true;

        } catch (IOException e) {
            Log.e(HttpUtil.class.getSimpleName(), "Could not fetch " + urlString, e);
            if (file.exists() && file.length() == 0) {
                boolean result = file.delete();
                if (!result) {
                    Log.w(HttpUtil.class.getSimpleName(), "Could not tidy up file " + file);
                }
            }
            return false;

        } finally {
            IOUtil.closeQuietly(conn);
            IOUtil.closeQuietly(in);
            IOUtil.closeQuietly(out);
        }
    }


    public static void openUrl(String url, final Context context) {
        Uri destination = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, destination);
        if (intent.resolveActivity(context.getPackageManager()) != null) {
            context.startActivity(intent);
        }
    }
}
