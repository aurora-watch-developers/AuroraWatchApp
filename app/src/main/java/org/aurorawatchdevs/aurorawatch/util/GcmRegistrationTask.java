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
public class GcmRegistrationTask extends AsyncTask<Void, Void, Void> {

    GoogleCloudMessaging mGcm;
    Context mContext;
    String mSENDER_ID;
    public String mRegistrationId;
    String mPROPERTY_REG_ID = "registration_id";
    String mPROPERTY_APP_VERSION = "appVersion";
    String mAppName;
    int mAppVersion;

    public GcmRegistrationTask(GoogleCloudMessaging gcm, Context context, String SENDER_ID, int appVersion, String appName) {
        mGcm = gcm;
        mContext = context;
        mSENDER_ID = SENDER_ID;
        mAppName = appName;
        mAppVersion = appVersion;
    }

    @Override
    protected Void doInBackground(Void... params) {
        String msg = "";
        try {
            if (mGcm == null) {
                mGcm = GoogleCloudMessaging.getInstance(mContext);
            }
            mRegistrationId = mGcm.register(mSENDER_ID);
            msg = "Device registered, registration ID=" + mRegistrationId;
            Log.i(getClass().getSimpleName(), msg);
            storeRegistrationId(mRegistrationId, mAppVersion, mAppName);

        } catch (IOException ex) {
            msg = "Error :" + ex.getMessage();
            Log.e(getClass().getSimpleName(), msg);
            ex.printStackTrace();
            // If there is an error, don't just keep trying to register.
            // Require the user to click a button again, or perform
            // exponential back-off.
        }
        return null;
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

