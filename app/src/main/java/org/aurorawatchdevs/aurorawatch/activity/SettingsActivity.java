package org.aurorawatchdevs.aurorawatch.activity;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.GooglePlayServicesAvailabilityException;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.android.gms.common.AccountPicker;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

import org.aurorawatchdevs.aurorawatch.AlertLevel;
import org.aurorawatchdevs.aurorawatch.R;
import org.aurorawatchdevs.aurorawatch.util.AccountUtils;
import org.aurorawatchdevs.aurorawatch.util.SaveAlertPreferenceTask;

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
    private AlertLevel lastAlertLevel;
    private String accountName;
    private Activity activity;

    private void setAlertButton() {
        alertNone.setTextColor(alertLevel == AlertLevel.none ? Color.YELLOW : Color.WHITE);
        alertMin.setTextColor(alertLevel == AlertLevel.minor ? Color.YELLOW : Color.WHITE);
        alertAmber.setTextColor(alertLevel == AlertLevel.amber ? Color.YELLOW : Color.WHITE);
        alertRed.setTextColor(alertLevel == AlertLevel.red ? Color.YELLOW : Color.WHITE);
    }

    public void loadAlertLevel() {
        try {
            SharedPreferences settings = getSharedPreferences(appName, 0);
            alertLevel = AlertLevel.values()[settings.getInt("alertLevel", 0)];
            lastAlertLevel = alertLevel;
            setAlertButton();
        } catch (Exception ex) {
            Log.e(appName, "Loading prefs: " + ex.getMessage());
        }
    }

    public void saveAlertLevel() {
        try {
            //Post alert setting to the cloud...
            if (!SaveAlertSetting(alertLevel)) {
                //if that failed, switch back
                alertLevel = lastAlertLevel;
            } else {
                SharedPreferences settings = getSharedPreferences(appName, 0);
                SharedPreferences.Editor editor = settings.edit();
                editor.putInt("alertLevel", alertLevel.ordinal());
                editor.apply();
            }
            setAlertButton();
        } catch (Exception ex) {
            Log.e(appName, "Saving prefs: " + ex.getMessage());
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
        activity = this;

        loadAlertLevel();

        alertNone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertLevel = AlertLevel.none;
                saveAlertLevel();
            }
        });

        alertMin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertLevel = AlertLevel.minor;
                saveAlertLevel();
            }
        });

        alertAmber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertLevel = AlertLevel.amber;
                saveAlertLevel();
            }
        });

        alertRed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertLevel = AlertLevel.red;
                saveAlertLevel();
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
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

    private boolean SaveAlertSetting(AlertLevel alertLevel) {
        if (!checkUserAccount())
            return false;

        if (!isDeviceOnline())
        {
            Toast.makeText(this,getResources().getString(R.string.not_online),Toast.LENGTH_SHORT).show();
            return false;
        }

        new SaveAlertPreferenceTask(this, accountName, SCOPE, alertLevel.name()).execute();
        return true;
    }

    static final int REQUEST_CODE_RECOVER_PLAY_SERVICES = 1001;
    static final int REQUEST_CODE_PICK_ACCOUNT = 1002;
    static final int REQUEST_CODE_RECOVER_FROM_PLAY_SERVICES_ERROR = 1001;

    //private static final String SCOPE =
    //        "oauth2:https://www.googleapis.com/auth/userinfo.profile";
    private static final String SCOPE = "audience:server:client_id:675205179905-34iahvkv5efvk3n5tbbt9qkr7en0hup3.apps.googleusercontent.com";
    //private static final String SCOPE = "audience:server:client_id:675205179905-kbg5ckfhlsmn801vneo5f8v8bgq2mmd8.apps.googleusercontent.com";

    private boolean checkUserAccount() {
        accountName = AccountUtils.getAccountName(this);
        if (accountName == null) {
            // Then the user was not found in the SharedPreferences. Either the
            // application deliberately removed the account, or the application's
            // data has been forcefully erased.
            showAccountPicker();
            return false;
        }

        Account account = AccountUtils.getGoogleAccountByName(this, accountName);
        if (account == null) {
            // Then the account has since been removed.
            AccountUtils.removeAccount(this);
            showAccountPicker();
            return false;
        }

        return true;
    }

    private void showAccountPicker() {
        Intent pickAccountIntent = AccountPicker.newChooseAccountIntent(
                null, null, new String[]{GoogleAuthUtil.GOOGLE_ACCOUNT_TYPE},
                true, null, null, null, null);
        startActivityForResult(pickAccountIntent, REQUEST_CODE_PICK_ACCOUNT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CODE_RECOVER_PLAY_SERVICES:
      /* ... */
            case REQUEST_CODE_PICK_ACCOUNT:
                if (resultCode == RESULT_OK) {
                    accountName = data.getStringExtra(
                            AccountManager.KEY_ACCOUNT_NAME);
                    AccountUtils.setAccountName(this, accountName);
                } else if (resultCode == RESULT_CANCELED) {
                    Toast.makeText(this, "This application requires a Google account.",
                            Toast.LENGTH_SHORT).show();
                    finish();
                }
                return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * This method is a hook for background threads and async tasks that need to
     * provide the user a response UI when an exception occurs.
     */
    public void handleException(final Exception e) {
        // Because this call comes from the AsyncTask, we must ensure that the following
        // code instead executes on the UI thread.
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (e instanceof GooglePlayServicesAvailabilityException) {
                    // The Google Play services APK is old, disabled, or not present.
                    // Show a dialog created by Google Play services that allows
                    // the user to update the APK
                    int statusCode = ((GooglePlayServicesAvailabilityException) e)
                            .getConnectionStatusCode();
                    Dialog dialog = GooglePlayServicesUtil.getErrorDialog(statusCode, activity ,
                            REQUEST_CODE_RECOVER_FROM_PLAY_SERVICES_ERROR);
                    dialog.show();
                } else if (e instanceof UserRecoverableAuthException) {
                    // Unable to authenticate, such as when the user has not yet granted
                    // the app access to the account, but the user can fix this.
                    // Forward the user to an activity in Google Play services.
                    Intent intent = ((UserRecoverableAuthException) e).getIntent();
                    startActivityForResult(intent,
                            REQUEST_CODE_RECOVER_FROM_PLAY_SERVICES_ERROR);
                }
                else
                {
                    Toast.makeText(activity,"An exception occurred while saving your alert setting: "
                            + e.getMessage(),Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private boolean checkPlayServices() {
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (status != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(status)) {
                showErrorDialog(status);
            } else {
                Toast.makeText(this, "This device is not supported.",
                        Toast.LENGTH_LONG).show();
                finish();
            }
            return false;
        }
        return true;
    }

    void showErrorDialog(int code) {
        GooglePlayServicesUtil.getErrorDialog(code, this,
                REQUEST_CODE_RECOVER_PLAY_SERVICES).show();
    }

    private boolean isDeviceOnline() {
            ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
            NetworkInfo ni = cm.getActiveNetworkInfo();
            if (ni == null) {
                // There are no active networks.
                return false;
            }
            return ni.isConnected();
    }
}