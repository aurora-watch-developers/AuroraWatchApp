package uk.ac.lancs.aurorawatch;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.util.Log;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import javaEventing.EventManager;
import javaEventing.EventObject;

/**
 * Created by jamesb on 11/02/2015.
 */
public class ImageFunctions {

    public static Bitmap getResizedBitmap(Bitmap bm, int newHeight) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleHeight = ((float) newHeight) / height;


        // create a matrix for the manipulation
        Matrix matrix = new Matrix();

        // resize the bitmap
        matrix.postScale(scaleHeight, scaleHeight);

        // recreate the new Bitmap
        Bitmap resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, false);

        return resizedBitmap;
    }

    public void downloadImageToFile(String imageurl, String filename) {
        InputStream in = null;

        try {
            Log.i("URL", imageurl);
            URL url = new URL(imageurl);
            URLConnection urlConn = url.openConnection();

            HttpURLConnection httpConn = (HttpURLConnection) urlConn;

            httpConn.connect();

            in = httpConn.getInputStream();

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Bitmap bmp = BitmapFactory.decodeStream(in);

        FileOutputStream out = null;
        try {
            out = new FileOutputStream(filename);
            bmp.compress(Bitmap.CompressFormat.PNG, 100, out);

            //raise event here.
            EventManager.triggerEvent(this, new ImageDownloadEvent());

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    class ImageDownloadEvent extends EventObject {}
}