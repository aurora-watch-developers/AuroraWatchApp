package uk.ac.lancs.aurorawatch.fragment;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.os.Handler;

import javaEventing.EventManager;
import javaEventing.interfaces.Event;
import javaEventing.interfaces.GenericEventListener;
import uk.ac.lancs.aurorawatch.ImageFunctions;
import uk.ac.lancs.aurorawatch.R;

/**
 * Fragment showing the past 24 hours
 */
public class Past24HrFragment extends Fragment {

    ImageView image;
    String savedFile;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        View view = getView();
        if (view != null) {
            image = (ImageView) getActivity().findViewById(R.id.imgPast24Hrs);
            savedFile = getActivity().getFilesDir() + "/24hr.png";

            //register event
            EventManager.registerEventListener(new GenericEventListener() {
                public void eventTriggered(Object sender, Event event) {
                    refreshImage();
                }
            }, ImageFunctions.ImageDownloadEvent.class);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        //refresh the image
        DownloadImage();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.past24hr, container, false);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public static Past24HrFragment newInstance() {
        return new Past24HrFragment();
    }

    private void DownloadImage() {
        String url = "http://aurorawatch.lancs.ac.uk/summary/aurorawatch_new/plots/awn_lan1/rolling.png";
        ImageFunctions imageFunctions = new ImageFunctions();
        imageFunctions.downloadImageToFile(url, savedFile);
    }

    private void refreshImage() {

        try{
            final Bitmap bmp = BitmapFactory.decodeFile(savedFile);
            if (bmp != null)
                image.setImageBitmap(bmp);
        }
        catch (Exception ex)
        {
            Log.e(getString(R.string.appName), "Loading 24hr image, " + ex.getMessage());
        }
    }
}