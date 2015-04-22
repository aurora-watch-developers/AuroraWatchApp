package org.aurorawatchdevs.aurorawatch;

import android.os.Bundle;
import android.preference.PreferenceActivity;

import org.aurorawatchdevs.aurorawatch.R;

/**
 * Created by Craig on 23/01/2015.
 */
public class Preferences extends PreferenceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }

//	@Override
//	protected void onResume() {
//		super.onResume();
//	}

//	@Override
//	protected void onPause() {
//		super.onPause();
//	}
}
