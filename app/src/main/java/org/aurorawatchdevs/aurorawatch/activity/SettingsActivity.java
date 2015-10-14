package org.aurorawatchdevs.aurorawatch.activity;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.GooglePlayServicesAvailabilityException;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.android.gms.common.AccountPicker;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import org.aurorawatchdevs.aurorawatch.AlertLevel;
import org.aurorawatchdevs.aurorawatch.R;
import org.aurorawatchdevs.aurorawatch.RangeSeekBar;
import org.aurorawatchdevs.aurorawatch.util.AccountUtils;
import org.aurorawatchdevs.aurorawatch.util.GcmRegistrationTask;
import org.aurorawatchdevs.aurorawatch.util.IAsyncFetchListener;
import org.aurorawatchdevs.aurorawatch.util.SaveAlertPreferenceTask;

import java.io.IOException;

/**
 * Activity for application settings, including alert level.
 */
public class SettingsActivity extends ActionBarActivity {


    static final int REQUEST_CODE_RECOVER_PLAY_SERVICES = 1001;
    static final int REQUEST_CODE_PICK_ACCOUNT = 1002;
    static final int REQUEST_CODE_RECOVER_FROM_PLAY_SERVICES_ERROR = 1001;
    static final String PROPERTY_REG_ID = "registration_id";
    static final String PROPERTY_APP_VERSION = "appVersion";
    static final String SCOPE = "audience:server:client_id:675205179905-34iahvkv5efvk3n5tbbt9qkr7en0hup3.apps.googleusercontent.com";

    private Button alertNone;
    private Button alertMin;
    private Button alertAmber;
    private Button alertRed;

    private RangeSeekBar alertTimeSlider;
    private LinearLayout alertTimeSliderContainer;

    private String appName;
    private AlertLevel alertLevel;
    private AlertLevel lastAlertLevel;
    private String accountName;
    private Activity activity;
    private String registrationId;
    private int appVersion;
    private GoogleCloudMessaging gcm;
    private String SENDER_ID = "675205179905";
    private ProgressDialog gcmProgressDialog;

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
        //Check we're online
        if (!isDeviceOnline()) {
            Toast.makeText(this, getResources().getString(R.string.not_online), Toast.LENGTH_SHORT).show();
            UnSaveUiState();
        }

        //Check / Prompt for account
        if (checkUserAccount())
            ContinueSavingAlertSettings(alertLevel);
    }

    private void OnSaveSuccess()
    {
        SaveAlertLevelToDevice();
        setAlertButton();
    }

    private void OnSaveFail()
    {
        UnSaveUiState();
    }

    private void UnSaveUiState() {
        Log.i("AuroraWatch","UnSaveUiState called");
        alertLevel = lastAlertLevel;
        setAlertButton();
    }

    private void SaveAlertLevelToDevice() {
        SharedPreferences settings = getSharedPreferences(appName, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt("alertLevel", alertLevel.ordinal());
        editor.apply();
    }

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.settings);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        alertTimeSliderContainer = (LinearLayout)findViewById(R.id.alertTimeSliderContainer);
        alertTimeSlider = new RangeSeekBar(0,24,this);
        alertTimeSliderContainer.addView(alertTimeSlider);

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

    private void ContinueSavingAlertSettings(final AlertLevel alertLevel)
    {
        if (checkPlayServices()) {
            gcm = GoogleCloudMessaging.getInstance(this);
            registrationId = getRegistrationId(getApplicationContext());

            if (registrationId.isEmpty()) {
                gcmProgressDialog = ProgressDialog.show(activity,getResources().getString(R.string.pleasewait),"Registering with Google Cloud Services");
                final GcmRegistrationTask registrationTask = new GcmRegistrationTask(gcm, this, SENDER_ID, appVersion, appName);
                registrationTask.setListener(new IAsyncFetchListener() {
                    public void onComplete(final String result) {
                        runOnUiThread(new Runnable() {
                            public void run() {
                                if (gcmProgressDialog != null)
                                    gcmProgressDialog.dismiss();

                                if (result == "SUCCESS") {
                                    registrationId = registrationTask.mRegistrationId;
                                    SaveAlertPreference((SettingsActivity)activity, accountName, SCOPE, alertLevel.name(), registrationId);
                                } else {
                                    Toast.makeText(getApplicationContext(), "Registration with Google Cloud Services failed", Toast.LENGTH_SHORT).show();
                                    OnSaveFail();
                                }
                            }
                        });
                    }
                });
                registrationTask.execute();
            }
            else {
                SaveAlertPreference(this, accountName, SCOPE, alertLevel.name(), registrationId);
            }
        }
        else
        {
            OnSaveFail();
        }

    }

    private void SaveAlertPreference(final SettingsActivity activity, String accountName, String scope, String alertLevel, String registrationId)
    {
        SaveAlertPreferenceTask saveAlertPreferenceTask = new SaveAlertPreferenceTask(activity, accountName, scope, alertLevel, registrationId);

        gcmProgressDialog = ProgressDialog.show(activity,getResources().getString(R.string.pleasewait),"Saving your alert setting...");
        saveAlertPreferenceTask.setListener(new IAsyncFetchListener() {
            public void onComplete(final String result) {
                runOnUiThread(new Runnable() {
                    public void run() {
                        if (gcmProgressDialog != null)
                            gcmProgressDialog.dismiss();

                        if (result == "ERR") {
                            Toast.makeText(getApplicationContext(), "Saving your alert preference failed", Toast.LENGTH_SHORT).show();
                            OnSaveFail();
                        }
                        else
                        {
                            Toast.makeText(activity,"Alert preference was saved successfully",Toast.LENGTH_SHORT).show();
                            OnSaveSuccess();
                        }
                    }
                });
            }
        });
        Log.i("AuroraWatch", "Calling saveAlertPreferenceTask for " + accountName + ", alertLevel:" + alertLevel + ", regId:" + registrationId);
        saveAlertPreferenceTask.execute();
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
                    Log.i("AuroraWatch","AccountPicker returned OK for " + accountName);
                    ContinueSavingAlertSettings(alertLevel);
                } else if (resultCode == RESULT_CANCELED) {
                    Toast.makeText(this, "This application requires a Google account.",
                            Toast.LENGTH_SHORT).show();
                    UnSaveUiState();
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
                    Dialog dialog = GooglePlayServicesUtil.getErrorDialog(statusCode, activity,
                            REQUEST_CODE_RECOVER_FROM_PLAY_SERVICES_ERROR);
                    dialog.show();
                } else if (e instanceof UserRecoverableAuthException) {
                    // Unable to authenticate, such as when the user has not yet granted
                    // the app access to the account, but the user can fix this.
                    // Forward the user to an activity in Google Play services.
                    Intent intent = ((UserRecoverableAuthException) e).getIntent();
                    startActivityForResult(intent,
                            REQUEST_CODE_RECOVER_FROM_PLAY_SERVICES_ERROR);
                } else {
                    Toast.makeText(activity, "An exception occurred while saving your alert setting: "
                            + e.getMessage(), Toast.LENGTH_LONG).show();
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

    private String getRegistrationId(Context context) {
        SharedPreferences settings = getSharedPreferences(appName, 0);
        String registrationId = settings.getString(PROPERTY_REG_ID, "");
        if (registrationId.isEmpty()) {
            Log.i(appName, "Registration not found.");
            return "";
        }
        // Check if app was updated; if so, it must clear the registration ID
        // since the existing registration ID is not guaranteed to work with
        // the new app version.
        int registeredVersion = settings.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);
        int currentVersion = getAppVersion(context);
        appVersion = currentVersion;
        if (registeredVersion != currentVersion) {
            Log.i(appName, "App version changed.");
            return "";
        }
        return registrationId;
    }

    private static int getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            // should never happen
            throw new RuntimeException("Could not get package name: " + e);
        }
    }
}