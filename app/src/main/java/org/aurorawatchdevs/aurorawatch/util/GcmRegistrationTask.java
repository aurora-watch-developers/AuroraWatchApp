package org.aurorawatchdevs.aurorawatch.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.io.IOException;

/**
 * Created by jamesb on 18/05/2015.
 */
public class GcmRegistrationTask extends AsyncTask<Void, Void, String> {

    GoogleCloudMessaging mGcm;
    Context mContext;
    String mSENDER_ID;
    public String mRegistrationId;
    String mPROPERTY_REG_ID = "registration_id";
    String mPROPERTY_APP_VERSION = "appVersion";
    String mAppName;
    int mAppVersion;
    public IAsyncFetchListener fetchListener = null;

    public GcmRegistrationTask(GoogleCloudMessaging gcm, Context context, String SENDER_ID, int appVersion, String appName) {
        mGcm = gcm;
        mContext = context;
        mSENDER_ID = SENDER_ID;
        mAppName = appName;
        mAppVersion = appVersion;
    }

    public void setListener(IAsyncFetchListener listener) {
        this.fetchListener = listener;
    }

    @Override
    protected String doInBackground(Void... params) {
        String msg = "";
        try {
            if (mGcm == null) {
                mGcm = GoogleCloudMessaging.getInstance(mContext);
            }
            mRegistrationId = mGcm.register(mSENDER_ID);
            msg = "Device registered, registration ID=" + mRegistrationId;
            Log.i(getClass().getSimpleName(), msg);
            storeRegistrationId(mRegistrationId, mAppVersion, mAppName);
            return "SUCCESS";
        } catch (IOException ex) {
            msg = "IO Error :" + ex.getMessage();
            Log.e(getClass().getSimpleName(), msg);
            ex.printStackTrace();
            return "FAIL";
        }
        catch (Exception ex) {
            msg = "Error :" + ex.getMessage();
            Log.e(getClass().getSimpleName(), msg);
            ex.printStackTrace();
            return "FAIL";
        }
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);

        if (fetchListener != null)
            fetchListener.onComplete(result);
    }

    private void storeRegistrationId(String registrationId, int appVersion, String appName) {
        SharedPreferences settings = mContext.getSharedPreferences(appName, 0);
        Log.i(appName, "Saving regId on app version " + appVersion);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(mPROPERTY_REG_ID, registrationId);
        editor.putInt(mPROPERTY_APP_VERSION, appVersion);
        editor.apply();
        editor.commit();
    }
}