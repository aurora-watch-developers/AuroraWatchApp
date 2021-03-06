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
public class SaveAlertPreferenceTask extends AsyncTask<Void,Void,String>  {
    SettingsActivity mActivity;
    String mScope;
    String mEmail;
    String mAlertLevel;
    String mRegistrationId;
    public IAsyncFetchListener fetchListener = null;

    public SaveAlertPreferenceTask(SettingsActivity activity, String name, String scope, String alertLevel, String registrationId) {
        this.mActivity = activity;
        this.mScope = scope;
        this.mEmail = name;
        this.mAlertLevel = alertLevel;
        this.mRegistrationId = registrationId;
    }

    public void setListener(IAsyncFetchListener listener) {
        this.fetchListener = listener;
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
    protected String doInBackground(Void... params) {
        try {
            String token = fetchToken();
            if (token != null) {
                //try and post the setting...
                Log.i("AuroraWatch", "Got AuroraWatch Token " + token);
                List<NameValuePair> httpParameters = new ArrayList<NameValuePair>();
                httpParameters.add(new BasicNameValuePair("token", token));
                httpParameters.add(new BasicNameValuePair("level", ConvertedAlertLevel(mAlertLevel)));
                httpParameters.add(new BasicNameValuePair("registrationId", mRegistrationId));
                Log.i("AuroraWatch", "Making Post request for alert level " + ConvertedAlertLevel(mAlertLevel));
                String success = HttpUtil.postRequest("https://aurora-watch-uk.appspot.com/saveAlertLevel", httpParameters);
                if (success.equals("ERR")) {
                    mActivity.handleException(new Exception("Token validation failed"));
                    return "ERR";
                }
                else
                return "OK";
            }
            else
            {
                Log.e("AuroraWatch","fetchToken() failed in SaveAlertPreferenceTask");
                return "ERR";
            }
        } catch (IOException e) {
            Log.e("AuroraWatch", "IO Error in SaveAlertPreferenceTask... ");
            e.printStackTrace();
            return "ERR";
        }
        catch (Exception e) {
            Log.e("AuroraWatch", "Error in SaveAlertPreferenceTask... ");
            e.printStackTrace();
            return "ERR";
        }
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);

        if (fetchListener != null)
            fetchListener.onComplete(result);
    }

    /**
     * Gets an authentication token from Google and handles any
     * GoogleAuthException that may occur.
     */
    protected String fetchToken() throws IOException {
        try {
            Log.i("AuroraWatch","Calling fetchToken...");
            return GoogleAuthUtil.getToken(mActivity, mEmail, mScope);
        } catch (UserRecoverableAuthException userRecoverableException) {
            mActivity.handleException(userRecoverableException);
        } catch (GoogleAuthException fatalException) {
            mActivity.handleException(fatalException);
        }
        return null;
    }
}
