package org.aurorawatchdevs.aurorawatch.activity;

import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.AccountPicker;

import org.aurorawatchdevs.aurorawatch.AlertLevel;
import org.aurorawatchdevs.aurorawatch.R;

/**
 * Activity for application settings, including alert level.
 */
public class SettingsActivity extends ActionBarActivity {

    private Button alertNone;
    private Button alertMin;
    private Button alertAmber;
    private Button alertRed;

    private String appName;
    private AlertLevel alertLevel;

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
            SharedPreferences settings = getSharedPreferences(appName, 0);
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
            //Post alert setting to the cloud...
            SaveAlertSetting(alertLevel);

            SharedPreferences settings = getSharedPreferences(appName, 0);
            SharedPreferences.Editor editor = settings.edit();
            editor.putInt("alertLevel", alertLevel.ordinal());
            editor.apply();
        }
        catch (Exception ex)
        {
            Log.e(appName,"Saving prefs: " + ex.getMessage());
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.settings);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        alertNone = (Button) findViewById(R.id.btnAlertNone);
        alertMin = (Button) findViewById(R.id.btnAlertMinor);
        alertAmber = (Button) findViewById(R.id.btnAlertAmber);
        alertRed = (Button) findViewById(R.id.btnAlertRed);
        appName = getString(R.string.appName);

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
    }

    @Override
    public void onPause()
    {
        super.onPause();
        saveAlertLevel();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
            default:
                Log.d("tag", Integer.toHexString(item.getItemId()));
                return super.onOptionsItemSelected(item);
        }
    }

    private void SaveAlertSetting(AlertLevel alertLevel)
    {
        pickUserAccount(this);
    }



    static final int REQUEST_CODE_PICK_ACCOUNT = 1000;
    String mEmail; // Received from newChooseAccountIntent(); passed to getToken()

    public void pickUserAccount(Activity activity) {
        String[] accountTypes = new String[]{"com.google"};
        Intent intent = AccountPicker.newChooseAccountIntent(null, null,
                accountTypes, false, null, null, null, null);
        activity.startActivityForResult(intent, REQUEST_CODE_PICK_ACCOUNT);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_PICK_ACCOUNT) {
            // Receiving a result from the AccountPicker
            if (resultCode == 0) {
                mEmail = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                // With the account name acquired, go get the auth token
                //TODO//getUsername();
            } else if (resultCode == 1) {
                // The account picker dialog closed without selecting an account.
                // Notify users that they must pick an account to proceed.
                Toast.makeText(this, R.string.pick_account, Toast.LENGTH_SHORT).show();
            }
        }
        // Later, more code will go here to handle the result from some exceptions...
    }
}
