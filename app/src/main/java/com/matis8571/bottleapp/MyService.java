package com.matis8571.bottleapp;


import static com.matis8571.bottleapp.Notifications.CHANNEL_1_ID;
import static com.matis8571.bottleapp.Notifications.CHANNEL_2_ID;
import static com.matis8571.bottleapp.Notifications.CHANNEL_3_ID;

import android.app.Notification;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class MyService extends Service {
    private static final String TAG = "MyService";
    private NotificationManagerCompat notificationManager;
    DateAndTime dateAndTime = new DateAndTime();
    //TODO add refresh method
    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate: Starting");
        notificationManager = NotificationManagerCompat.from(this);
        SharedPreferences mainPrefsReceiver = getApplicationContext().getSharedPreferences(
                "mainPrefs", Context.MODE_PRIVATE);
        SharedPreferences userProfilePrefsReceiver = getApplicationContext().getSharedPreferences(
                "userProfilePrefs", Context.MODE_PRIVATE);
        int filterEfficiency = userProfilePrefsReceiver.getInt("filterEfficiency", 0);
        int countFilterEfficiency = mainPrefsReceiver.getInt("countFilterEfficiency", 0);
        int howMuchToDrink = mainPrefsReceiver.getInt("howMuchToDrink", 0);
        int countDaysToFilterChange = mainPrefsReceiver.getInt("countDaysToFilterChange", 0);

        //send notifications for the last 3 days of filter usage set date
        if (countDaysToFilterChange >= 0) {
            switch (countDaysToFilterChange) {
                case 3:
                case 2:
                case 1:
                case 0:
                    notificationCh1DaysLeft();
                    break;
            }
        }

        //check if user consumed settled amount of water, if not, send notifications at fixed hours
        if (howMuchToDrink > 0) {
            switch (dateAndTime.getTimeHour()) {
                case 10:
                case 14:
                case 18:
                    notificationCh2DrinkReminder();
                    break;
            }
        }

        //check if filter is still usable
        // send notifications for the last 5 days of settled usage amount
        if (countFilterEfficiency <= filterEfficiency) {
            switch (countFilterEfficiency - filterEfficiency) {
                case 5:
                case 3:
                case 2:
                case 1:
                case 0:
                    notificationCh3FilterEfficiencyWater();
                    break;
            }
        }
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy: Destroy");
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     * Builds new notification message with custom properties (Title, Test and Icon required)
     * on previously set channels. Then calls NotificationManagerCompat with .notify to call for a
     * notification to show on phone screen.
     */
    public void notificationCh1DaysLeft() {
        SharedPreferences mainPrefsReceiver = getApplicationContext().getSharedPreferences(
                "mainPrefs", Context.MODE_PRIVATE);
        int countDaysToFilterChange = mainPrefsReceiver.getInt("countDaysToFilterChange", 0);

        String notificationCh1Title = "BottleApp";
        String notificationCh1Message;

        if (countDaysToFilterChange == 0) {
            notificationCh1Message = countDaysToFilterChange + "Days left\nChange filter today!";
        } else {
            notificationCh1Message = "Days left to filter change: " + countDaysToFilterChange;
        }

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_1_ID)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(notificationCh1Title)
                .setContentText(notificationCh1Message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .build();
        notificationManager.notify(1, notification);
    }

    public void notificationCh2DrinkReminder() {
        String title = "BottleApp";
        String message = "Don't forget to drink more water!";

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_2_ID)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .build();
        notificationManager.notify(2, notification);
    }

    public void notificationCh3FilterEfficiencyWater() {
        SharedPreferences mainPrefsReceiver = getApplicationContext().getSharedPreferences(
                "mainPrefs", Context.MODE_PRIVATE);
        SharedPreferences userProfilePrefsReceiver = getApplicationContext().getSharedPreferences(
                "userProfilePrefs", Context.MODE_PRIVATE);
        int filterEfficiency = userProfilePrefsReceiver.getInt("filterEfficiency", 0);
        int countFilterEfficiency = mainPrefsReceiver.getInt("countFilterEfficiency", 0);
        int howMuchToFilterLeft = filterEfficiency - countFilterEfficiency;

        String title = "BottleApp";
        String message;
        if (howMuchToFilterLeft != 0) {
            message = "Water flows\nFilter have only: " + howMuchToFilterLeft + "l left to change!";
        } else {
            message = "Water flows\nChange filter today!";
        }

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_3_ID)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .build();
        notificationManager.notify(3, notification);
    }
}
