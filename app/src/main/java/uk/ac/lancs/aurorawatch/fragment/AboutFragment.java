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
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;

import uk.ac.lancs.aurorawatch.R;
import uk.ac.lancs.aurorawatch.util.HttpUtil;
import uk.ac.lancs.aurorawatch.service.Past24HrService;

/**
 * Fragment showing the past 24 hours
 */
public class AboutFragment extends Fragment {

    TextView txtUrlAuroraWatch;
    TextView txtUrlLancaster;
    TextView txtUrlSamnet;
    TextView txtUrlAuroraNet;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        View view = getView();
        txtUrlAuroraWatch = (TextView)view.findViewById(R.id.urlAuroraWatchUK);
        txtUrlLancaster = (TextView)view.findViewById(R.id.urlLancsUni);
        txtUrlSamnet = (TextView)view.findViewById(R.id.urlSamnet);
        txtUrlAuroraNet = (TextView)view.findViewById(R.id.urlAuroraWatchNet);

        txtUrlAuroraWatch.setMovementMethod(LinkMovementMethod.getInstance());
        txtUrlLancaster.setMovementMethod(LinkMovementMethod.getInstance());
        txtUrlSamnet.setMovementMethod(LinkMovementMethod.getInstance());
        txtUrlAuroraNet.setMovementMethod(LinkMovementMethod.getInstance());
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.about, container, false);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public static AboutFragment newInstance() {
        return new AboutFragment();
    }
}