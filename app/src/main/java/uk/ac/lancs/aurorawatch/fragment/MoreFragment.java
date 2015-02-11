package uk.ac.lancs.aurorawatch.fragment;

import android.support.v4.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
    }

    private void openUrl(String url) {
        Uri destination = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, destination);
        if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivity(intent);
        }
    }
}
