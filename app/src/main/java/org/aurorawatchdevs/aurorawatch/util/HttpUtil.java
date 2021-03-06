package org.aurorawatchdevs.aurorawatch.util;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpParams;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import org.apache.http.util.EntityUtils;
import org.aurorawatchdevs.aurorawatch.R;

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
            conn.setRequestProperty("User-Agent", context.getResources().getString(R.string.appIdentifier));
            Log.d(HttpUtil.class.getSimpleName(), "User agent set: " + conn.getRequestProperties().get("User-Agent").toString());
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

    public static String postRequest(String url, List<NameValuePair> params) {
        HttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(url);
        try {
            httpPost.setEntity(new UrlEncodedFormEntity(params));
            HttpResponse response = httpClient.execute(httpPost);
            Log.i(HttpUtil.class.getSimpleName(), "AuroraWatch alert request status: "
                    + response.getStatusLine());
            String serverResponse = EntityUtils.toString(response.getEntity());
                    Log.i(HttpUtil.class.getSimpleName(), "AuroraWatch alert request response: "
                            + serverResponse);
            if (serverResponse.contains("TokenCheckerService$TokenValidationException"))
            {
                throw new Exception("TokenValidationException");
            }
            return "OK";
        }
        catch (UnsupportedEncodingException unsupportedEncodingException) {
            Log.e(HttpUtil.class.getSimpleName(), "UnsupportedEncodingException");
            unsupportedEncodingException.printStackTrace();
        }
        catch (ClientProtocolException clientProtocolException){
            Log.e(HttpUtil.class.getSimpleName(), "ClientProtocolException");
            clientProtocolException.printStackTrace();
        }
        catch (IOException ioException){
            Log.e(HttpUtil.class.getSimpleName(), "IOException");
            ioException.printStackTrace();
        }
        catch (Exception exception)
        {
            Log.e(HttpUtil.class.getSimpleName(), exception.getMessage());
            exception.printStackTrace();
        }
        return "ERR";
    }
}
