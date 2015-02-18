package uk.ac.lancs.aurorawatch.service;

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
 * HTTP utilities.
 */
public class HttpUtil {

    public static boolean downloadFile(String urlString, String path) {
        HttpURLConnection conn = null;
        InputStream in = null;
        OutputStream out = null;


        File file = new File(path);
        try {

            URL url = new URL(urlString);

            Log.i(HttpUtil.class.getSimpleName(), "Fetching " + url.toString());
            conn = (HttpURLConnection) url.openConnection();
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
    }
}
