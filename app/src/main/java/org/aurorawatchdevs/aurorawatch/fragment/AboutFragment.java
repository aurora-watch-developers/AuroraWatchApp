package org.aurorawatchdevs.aurorawatch.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.aurorawatchdevs.aurorawatch.R;

/**
 * Fragment showing the past 24 hours
 */
public class AboutFragment extends Fragment {

    TextView txtUrlAuroraWatch;
    TextView txtUrlLancaster;
    TextView txtUrlSamnet;
    TextView txtUrlAuroraNet;
    TextView txtUrlSupport;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        View view = getView();
        txtUrlAuroraWatch = (TextView)view.findViewById(R.id.urlAuroraWatchUK);
        txtUrlLancaster = (TextView)view.findViewById(R.id.urlLancsUni);
        txtUrlSamnet = (TextView)view.findViewById(R.id.urlSamnet);
        txtUrlAuroraNet = (TextView)view.findViewById(R.id.urlAuroraWatchNet);
        txtUrlSupport = (TextView)view.findViewById(R.id.urlSupport);

        txtUrlAuroraWatch.setMovementMethod(LinkMovementMethod.getInstance());
        txtUrlLancaster.setMovementMethod(LinkMovementMethod.getInstance());
        txtUrlSamnet.setMovementMethod(LinkMovementMethod.getInstance());
        txtUrlAuroraNet.setMovementMethod(LinkMovementMethod.getInstance());
        txtUrlSupport.setMovementMethod(LinkMovementMethod.getInstance());
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