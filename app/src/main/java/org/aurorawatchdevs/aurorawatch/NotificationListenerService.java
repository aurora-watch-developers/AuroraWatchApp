package org.aurorawatchdevs.aurorawatch;

import android.os.Bundle;
import android.util.Log;
import com.google.android.gms.gcm.GcmListenerService;

import org.aurorawatchdevs.aurorawatch.AlertLevel;
import org.aurorawatchdevs.aurorawatch.Notification;

/**
 * Created by jamesb on 05/06/2015.
 */
public class NotificationListenerService extends GcmListenerService {
    @Override
    public void onMessageReceived(String from, Bundle data) {
        String message = data.getString("message");
        Log.d("AuroraWatchUK", "From: " + from);
        Log.d("AuroraWatchUK", "Message: " + message);

        //Here we might want to trigger a download of 'now' data?

        Notification.ShowNotification(message, getApplicationContext());  //TODO - get from Message?
    }
}
