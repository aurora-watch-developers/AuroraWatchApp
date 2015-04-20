package uk.ac.lancs.aurorawatch.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ScaleDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.RelativeLayout;

import uk.ac.lancs.aurorawatch.ImageFunctions;
import uk.ac.lancs.aurorawatch.R;
import uk.ac.lancs.aurorawatch.util.HttpUtil;

/**
 * Created by jamesb on 04/02/2015.
 */
public class MoreFragment extends Fragment {

    RelativeLayout twitterButton;
    RelativeLayout facebookButton;
    RelativeLayout flickrButton;
    RelativeLayout faqButton;
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
        faqButton = (RelativeLayout)getView().findViewById(R.id.faqButton);
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
