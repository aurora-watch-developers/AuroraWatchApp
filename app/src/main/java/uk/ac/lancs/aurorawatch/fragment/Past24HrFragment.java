package uk.ac.lancs.aurorawatch.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

import java.io.File;

import uk.ac.lancs.aurorawatch.R;
import uk.ac.lancs.aurorawatch.util.HttpUtil;
import uk.ac.lancs.aurorawatch.service.Past24HrService;

/**
 * Fragment showing the past 24 hours
 */
public class Past24HrFragment extends Fragment {

    ImageView image;
    String savedFile;
    Boolean shown = false;
    private BroadcastReceiver receiver;
    private String cacheFile;


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        View view = getView();
        if (view != null) {
            image = (ImageView) getActivity().findViewById(R.id.imgPast24Hrs);
            this.getView().getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    if (!shown) {
                        shown = true;
                        //show a cached image, if present
                        updateUI(cacheFile);
                    }
                }
            });
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                updateUI(intent.getStringExtra(Past24HrService.CACHE_FILE));
            }
        };

        File cacheDir = getActivity().getCacheDir();
        cacheFile = new File(cacheDir, "24hr.png").getAbsolutePath();

        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(
                receiver,
                new IntentFilter(Past24HrService.FETCH_24HR_IMAGE));

        Past24HrService.startFetch24HrImage(getActivity(), cacheFile);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.past24hr, container, false);
    }

    @Override
    public void onDestroy() {
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(receiver);
        super.onDestroy();
    }

    public static Past24HrFragment newInstance() {
        return new Past24HrFragment();
    }

    private void updateUI(String cacheFile) {
        try{
            final Bitmap bmp = BitmapFactory.decodeFile(cacheFile);
            if (bmp != null)
                image.setImageBitmap(bmp);
        }
        catch (Exception ex)
        {
            Log.e(getString(R.string.appName), "Loading 24hr image, " + ex.getMessage());
        }
    }
}