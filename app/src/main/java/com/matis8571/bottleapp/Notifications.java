package com.matis8571.bottleapp;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

public class Notifications extends Service {
    private static final String TAG = "Notifications";
    public static final String CHANNEL_1_ID = "channel1";
    public static final String CHANNEL_2_ID = "channel2";
    public static final String CHANNEL_3_ID = "channel3";

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate: Starting");
        createNotificationChannels();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     * Use to create new channels for notifications.
     * Checks if current android version allows creation of notification channels
     * (not available under Oreo android version), then sets channel: id, name, and notification
     * importance settings along with description, and lastly creates channels with set properties.
     */
    public void createNotificationChannels() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel1 = new NotificationChannel(
                    CHANNEL_1_ID,
                    "Channel_1",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel1.setDescription("This is Channel_1");

            NotificationChannel channel2 = new NotificationChannel(
                    CHANNEL_2_ID,
                    "Channel_2",
                    NotificationManager.IMPORTANCE_LOW
            );
            channel2.setDescription("This is Channel_2");

            NotificationChannel channel3 = new NotificationChannel(
                    CHANNEL_3_ID,
                    "Channel_3",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel3.setDescription("This is Channel_3");
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager  = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel1);
            notificationManager.createNotificationChannel(channel2);
            notificationManager.createNotificationChannel(channel3);
        }
    }
}
