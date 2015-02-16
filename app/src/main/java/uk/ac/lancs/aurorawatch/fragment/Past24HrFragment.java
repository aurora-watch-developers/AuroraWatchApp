package uk.ac.lancs.aurorawatch.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import uk.ac.lancs.aurorawatch.R;

/**
 * Fragment showing the past 24 hours
 */
public class Past24HrFragment extends Fragment {

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        View view = getView();
        if (view != null) {


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
}
