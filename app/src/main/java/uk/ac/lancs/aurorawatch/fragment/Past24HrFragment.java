package uk.ac.lancs.aurorawatch.fragment;

import android.media.Image;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import uk.ac.lancs.aurorawatch.R;

/**
 * Fragment showing the past 24 hours
 */
public class Past24HrFragment extends Fragment {

    ImageView image;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        View view = getView();
        if (view != null) {
            image = (ImageView)getActivity().findViewById(R.id.imgPast24Hrs);

        }
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

    private void DownloadImage()
    {
        //http://aurorawatch.lancs.ac.uk/summary/aurorawatch_new/plots/awn_lan1/rolling.png
        
    }
}
