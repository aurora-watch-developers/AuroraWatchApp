package uk.ac.lancs.aurorawatch.fragment;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ScaleDrawable;
import android.support.v4.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageButton;

import uk.ac.lancs.aurorawatch.R;

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
                openUrl("https://twitter.com/aurorawatchuk");
            }
        });
        facebookButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openUrl("https://www.facebook.com/aurorawatchuk");
            }
        });
        flickrButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openUrl("http://www.flickr.com/groups/aurorawatch");
            }
        });
        websiteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openUrl("http://aurorawatch.lancs.ac.uk/");
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


    private void openUrl(String url) {
        Uri destination = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, destination);
        if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    private void resizeButtonImage(Button button)
    {
        Drawable[] buttonDrawables = button.getCompoundDrawables();
        Bitmap currentImage = ((BitmapDrawable)(buttonDrawables[0])).getBitmap();
        Bitmap resizedImage = ImageFunctions.getResizedBitmap(currentImage, button.getHeight());
        button.setCompoundDrawablesWithIntrinsicBounds(new BitmapDrawable(resizedImage),null,null,null);
    }
}
