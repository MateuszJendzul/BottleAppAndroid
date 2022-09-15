package com.matis8571.bottleapp;


import static com.matis8571.bottleapp.Notifications.CHANNEL_1_ID;
import static com.matis8571.bottleapp.Notifications.CHANNEL_2_ID;
import static com.matis8571.bottleapp.Notifications.CHANNEL_3_ID;

import android.app.Notification;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class MyService extends Service {
    private static final String TAG = "MyService";
    private NotificationManagerCompat notificationManager;
    final Handler handler = new Handler();
    final int delay = 500;
    private int xCh1, xCh2, xCh3;
    DateAndTime dateAndTime = new DateAndTime();

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate: Starting");
        notificationManager = NotificationManagerCompat.from(this);

        // Refresh containing code every second
        handler.postDelayed(new Runnable() {
            public void run() {
                //code to run below
                notify3DaysBeforeExpiration();
                dailyWaterConsumptionReminder();
                filterEfficiencyCheckReminder();
                handler.postDelayed(this, delay);
            }
        }, delay);
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy: Destroy");
        super.onDestroy();
    }

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
            notificationCh1Message = countDaysToFilterChange + " days left to filter change!";
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
            message = "Filter have only: " + howMuchToFilterLeft + "l left to change!";
        } else {
            message = "Filter used up, change it today!";
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

    /**
     * Check if filter is still usable, send notifications for the last 5 days of settled usage amount.
     */
    private void filterEfficiencyCheckReminder() {
        SharedPreferences mainPrefsReceiver = getApplicationContext().getSharedPreferences(
                "mainPrefs", Context.MODE_PRIVATE);
        SharedPreferences userProfilePrefsReceiver = getApplicationContext().getSharedPreferences(
                "userProfilePrefs", Context.MODE_PRIVATE);
        int filterEfficiency = userProfilePrefsReceiver.getInt("filterEfficiency", 0);
        int countFilterEfficiency = mainPrefsReceiver.getInt("countFilterEfficiency", 0);
        if (countFilterEfficiency <= filterEfficiency) {
            if (countFilterEfficiency - filterEfficiency <= 5 && dateAndTime.getDay() != xCh2) {
                notificationCh3FilterEfficiencyWater();
                xCh2 = dateAndTime.getDay();
            }
        }
    }

    /**
     * Every hour check if user consumed settled amount of water, if not, send notification.
     */
    private void dailyWaterConsumptionReminder() {
        SharedPreferences mainPrefsReceiver = getApplicationContext().getSharedPreferences(
                "mainPrefs", Context.MODE_PRIVATE);
        int howMuchToDrink = mainPrefsReceiver.getInt("howMuchToDrink", 0);
        if (howMuchToDrink > 0) {
            if (dateAndTime.getTimeHour() != xCh1 && dateAndTime.getTimeMinute() == 0
                    && dateAndTime.getTimeSeconds() == 0) {
                notificationCh2DrinkReminder();
                xCh1 = dateAndTime.getTimeHour();
            }
        }
    }

    /**
     * Send notifications for the last 3 days of filter usage set date.
     */
    private void notify3DaysBeforeExpiration() {
        SharedPreferences mainPrefsReceiver = getApplicationContext().getSharedPreferences(
                "mainPrefs", Context.MODE_PRIVATE);
        int countDaysToFilterChange = mainPrefsReceiver.getInt("countDaysToFilterChange", 0);
        if (countDaysToFilterChange <= 3 && dateAndTime.getDay() != xCh3) {
            notificationCh1DaysLeft();
            xCh3 = dateAndTime.getDay();
        }
    }
}
