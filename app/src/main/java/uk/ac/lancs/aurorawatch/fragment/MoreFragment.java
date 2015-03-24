package uk.ac.lancs.aurorawatch.fragment;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.content.SharedPreferences;

import uk.ac.lancs.aurorawatch.AlertLevel;
import uk.ac.lancs.aurorawatch.ImageFunctions;
import uk.ac.lancs.aurorawatch.R;
import uk.ac.lancs.aurorawatch.util.HttpUtil;

/**
 * Created by jamesb on 04/02/2015.
 */
public class MoreFragment extends Fragment {

    Button twitterButton;
    Button facebookButton;
    Button flickrButton;
    Button faqButton;
    Button websiteButton;
    Button alertNone;
    Button alertMin;
    Button alertAmber;
    Button alertRed;
    Boolean mMeasured = false;
    AlertLevel alertLevel;
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

        appName = getString(R.string.appName);
        twitterButton = (Button)getView().findViewById(R.id.twitterButton);
        facebookButton = (Button)getView().findViewById(R.id.facebookButton);
        flickrButton = (Button)getView().findViewById(R.id.flickrButton);
        faqButton = (Button)getView().findViewById(R.id.faqButton);
        websiteButton = (Button)getView().findViewById(R.id.awUrlButton);
        alertNone = (Button)getView().findViewById(R.id.btnAlertNone);
        alertMin = (Button)getView().findViewById(R.id.btnAlertMinor);
        alertAmber = (Button)getView().findViewById(R.id.btnAlertAmber);
        alertRed = (Button)getView().findViewById(R.id.btnAlertRed);

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
                HttpUtil.openUrl("http://aurorawatch.lancs.ac.uk/",getActivity());
            }
        });

        loadAlertLevel();

        alertNone.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                alertLevel = AlertLevel.none;
                setAlertButton();
            }
        });

        alertMin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertLevel = AlertLevel.minor;
                setAlertButton();
            }
        });

        alertAmber.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                alertLevel = AlertLevel.amber;
                setAlertButton();
            }
        });

        alertRed.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                alertLevel = AlertLevel.red;
                setAlertButton();
            }
        });



        this.getView().getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (!mMeasured) {
                    mMeasured = true;
                    resizeButtonIcons();
                }
            }
        });
    }

    @Override
    public void onPause()
    {
        super.onPause();
        saveAlertLevel();
    }

    public void resizeButtonIcons() {
        resizeButtonImage(twitterButton);
        resizeButtonImage(facebookButton);
        resizeButtonImage(flickrButton);
        resizeButtonImage(faqButton);
        resizeButtonImage(websiteButton);
    }

    private void resizeButtonImage(Button button)
    {
        Drawable[] buttonDrawables = button.getCompoundDrawables();
        Bitmap currentImage = ((BitmapDrawable)(buttonDrawables[0])).getBitmap();
        Bitmap resizedImage = ImageFunctions.getResizedBitmap(currentImage, button.getHeight());
        button.setCompoundDrawablesWithIntrinsicBounds(new BitmapDrawable(resizedImage),null,null,null);
    }

    private void setAlertButton()
    {
        alertNone.setTextColor(alertLevel == AlertLevel.none ?  Color.YELLOW : Color.WHITE);
        alertMin.setTextColor(alertLevel == AlertLevel.minor ?  Color.YELLOW : Color.WHITE);
        alertAmber.setTextColor(alertLevel == AlertLevel.amber ?  Color.YELLOW : Color.WHITE);
        alertRed.setTextColor(alertLevel == AlertLevel.red ?  Color.YELLOW : Color.WHITE);
    }

    public void loadAlertLevel()
    {
        try {
            SharedPreferences settings = getActivity().getSharedPreferences(appName, 0);
            alertLevel = AlertLevel.values()[settings.getInt("alertLevel", 0)];
            setAlertButton();
        }
        catch (Exception ex)
        {
            Log.e(appName, "Loading prefs: " + ex.getMessage());
        }
    }

    public void saveAlertLevel()
    {
        try {
            SharedPreferences settings = getActivity().getSharedPreferences(appName, 0);
            SharedPreferences.Editor editor = settings.edit();
            editor.putInt("alertLevel", alertLevel.ordinal());
            editor.commit();
        }
        catch (Exception ex)
        {
            Log.e(appName,"Saving prefs: " + ex.getMessage());
        }
    }

    public static MoreFragment newInstance() {
        return new MoreFragment();
        //If needed, arguments can be instatiated here using a Bundle.
    }
}
