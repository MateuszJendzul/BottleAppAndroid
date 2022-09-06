package com.matis8571.bottleapp;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class Notifications extends AppCompatActivity {
    public static final String CHANNEL_1_ID = "channel1";
    public static final String CHANNEL_2_ID = "channel2";
    public static final String CHANNEL_3_ID = "channel3";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        createNotificationChannels();
    }

    /**
     * Used to create new channels for notifications.
     * Checks if current android version allows creation of notification channels
     * (not available under Oreo android version), then sets channel: id, name, and notification
     * importance settings along with description, and lastly creates channels with set properties.
     */
    public void createNotificationChannels() {
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

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel1);
            manager.createNotificationChannel(channel2);
            manager.createNotificationChannel(channel3);
        }
    }
}
