package org.aurorawatchdevs.aurorawatch.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import org.aurorawatchdevs.aurorawatch.R;
import org.aurorawatchdevs.aurorawatch.util.HttpUtil;

/**
 * Created by jamesb on 04/02/2015.
 */
public class MoreFragment extends Fragment {

    RelativeLayout twitterButton;
    RelativeLayout facebookButton;
    RelativeLayout flickrButton;
    RelativeLayout websiteButton;

    Boolean mMeasured = false;

    String appName;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.more, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        twitterButton = (RelativeLayout)getView().findViewById(R.id.twitterButton);
        facebookButton = (RelativeLayout)getView().findViewById(R.id.facebookButton);
        flickrButton = (RelativeLayout)getView().findViewById(R.id.flickrButton);
        websiteButton = (RelativeLayout)getView().findViewById(R.id.awUrlButton);

        twitterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HttpUtil.openUrl("https://twitter.com/aurorawatchuk", getActivity());
            }
        });
        facebookButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HttpUtil.openUrl("https://www.facebook.com/aurorawatchuk", getActivity());
            }
        });
        flickrButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HttpUtil.openUrl("http://www.flickr.com/groups/aurorawatch", getActivity());
            }
        });
        websiteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HttpUtil.openUrl("http://aurorawatch.lancs.ac.uk/", getActivity());
            }
        });
    }

    public static MoreFragment newInstance() {
        return new MoreFragment();
        //If needed, arguments can be instatiated here using a Bundle.
    }
}
