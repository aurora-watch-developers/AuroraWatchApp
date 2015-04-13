package uk.ac.lancs.aurorawatch.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;

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

        twitterButton = (Button)getView().findViewById(R.id.twitterButton);
        facebookButton = (Button)getView().findViewById(R.id.facebookButton);
        flickrButton = (Button)getView().findViewById(R.id.flickrButton);
        faqButton = (Button)getView().findViewById(R.id.faqButton);
        websiteButton = (Button)getView().findViewById(R.id.awUrlButton);

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

    public static MoreFragment newInstance() {
        return new MoreFragment();
        //If needed, arguments can be instatiated here using a Bundle.
    }
}
