package uk.ac.lancs.aurorawatch;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Handler;
import android.util.Log;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

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
}