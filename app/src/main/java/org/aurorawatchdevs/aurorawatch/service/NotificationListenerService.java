package org.aurorawatchdevs.aurorawatch.service;

import android.os.Bundle;
import android.util.Log;
import com.google.android.gms.gcm.GcmListenerService;

/**
 * Created by jamesb on 05/06/2015.
 */
public class NotificationListenerService extends GcmListenerService {
    @Override
    public void onMessageReceived(String from, Bundle data) {
        String message = data.getString("message");
        Log.d("AuroraWatchUK", "From: " + from);
        Log.d("AuroraWatchUK", "Message: " + message);

        /**
         * Production applications would usually process the message here.
         * Eg: - Syncing with server.
         *     - Store message in local database.
         *     - Update UI.
         */

        /**
         * In some cases it may be useful to show a notification indicating to the user
         * that a message was received.
         */
        //TODO sendNotification(message);
    }
}
