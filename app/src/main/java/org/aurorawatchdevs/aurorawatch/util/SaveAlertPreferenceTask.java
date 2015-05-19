package org.aurorawatchdevs.aurorawatch.util;

import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.UserRecoverableAuthException;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.aurorawatchdevs.aurorawatch.activity.SettingsActivity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jamesb on 30/04/2015.
 */
public class SaveAlertPreferenceTask extends AsyncTask<Void,Void,Void>  {
    SettingsActivity mActivity;
    String mScope;
    String mEmail;
    String mAlertLevel;
    String mRegistrationId;

    public SaveAlertPreferenceTask(SettingsActivity activity, String name, String scope, String alertLevel, String registrationId) {
        this.mActivity = activity;
        this.mScope = scope;
        this.mEmail = name;
        this.mAlertLevel = alertLevel;
        this.mRegistrationId = registrationId;
    }

    private String ConvertedAlertLevel(String alertLevel)
    {
        switch (alertLevel)
        {
            case "test":
                return "TEST";
            case "minor":
                return  "YELLOW";
            case "amber":
                return "AMBER";
            case "red":
                return "RED";
            default:
                return "GREEN";
        }
    }

    /**
     * Executes the asynchronous job. This runs when you call execute()
     * on the AsyncTask instance.
     */
    @Override
    protected Void doInBackground(Void... params) {
        try {
            String token = fetchToken();
            if (token != null) {
                //try and post the setting...
                Log.i(getClass().getSimpleName(), "Got AuroraWatch Token " + token);
                List<NameValuePair> httpParameters = new ArrayList<NameValuePair>();
                httpParameters.add(new BasicNameValuePair("token", token));
                httpParameters.add(new BasicNameValuePair("level", ConvertedAlertLevel(mAlertLevel)));
                httpParameters.add(new BasicNameValuePair("registrationId", mRegistrationId));
                Log.i(getClass().getSimpleName(), "Making Post request for alert level " + ConvertedAlertLevel(mAlertLevel));
                HttpUtil.postRequest("https://aurora-watch-uk.appspot.com/saveAlertLevel", httpParameters);
            }
        } catch (IOException e) {
            Log.e(getClass().getSimpleName(), "IO Error in SaveAlertPreferenceTask... ");
            e.printStackTrace();
        }
        catch (Exception e) {
            Log.e(getClass().getSimpleName(), "Error in SaveAlertPreferenceTask... ");
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Gets an authentication token from Google and handles any
     * GoogleAuthException that may occur.
     */
    protected String fetchToken() throws IOException {
        try {
            return GoogleAuthUtil.getToken(mActivity, mEmail, mScope);
        } catch (UserRecoverableAuthException userRecoverableException) {
            mActivity.handleException(userRecoverableException);
        } catch (GoogleAuthException fatalException) {
            mActivity.handleException(fatalException);
        }
        return null;
    }
}
