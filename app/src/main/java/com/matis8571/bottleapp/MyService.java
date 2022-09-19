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
    private final Handler handler = new Handler();
    private final int delay = 1000;
    DateAndTime dateAndTime = new DateAndTime();

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate: Starting");
        notificationManager = NotificationManagerCompat.from(this);
        SharedPreferences mainPrefsReceiver = getApplicationContext().getSharedPreferences(
                "mainPrefs", Context.MODE_PRIVATE);
        SharedPreferences userProfilePrefsReceiver = getApplicationContext().getSharedPreferences(
                "userProfilePrefs", Context.MODE_PRIVATE);
        SharedPreferences filterPrefsReceiver = getApplicationContext().getSharedPreferences(
                "filterPrefs", Context.MODE_PRIVATE);
        SharedPreferences mainPrefs = getApplicationContext().getSharedPreferences(
                "mainPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor mainPrefsEditor = mainPrefs.edit();
        boolean enableShowProfileButton = filterPrefsReceiver.getBoolean("enableShowProfileButton", false);
        int daysToFilterChangeCounting = mainPrefsReceiver.getInt("daysToFilterChangeCounting", 0);
        int howMuchToDrink = mainPrefsReceiver.getInt("howMuchToDrink", 0);
        int filterEfficiencyCounting = mainPrefsReceiver.getInt("filterEfficiencyCounting", 0);
        int filterEfficiency = userProfilePrefsReceiver.getInt("filterEfficiency", 0);
        int howMuchToFilterLeft = filterEfficiency - (filterEfficiencyCounting / 1000);

        // Refresh containing code every value (seconds) defined by delay variable
        if (enableShowProfileButton) {
            //noinspection FieldMayBeFinal
            handler.postDelayed(new Runnable() {
                int xCh1 = mainPrefsReceiver.getInt("xCh1", 0);
                int xCh2 = mainPrefsReceiver.getInt("xCh2", 0);
                int xCh3 = mainPrefsReceiver.getInt("xCh3", 0);

                public void run() { //code to run below
                    // Send notifications for the last 3 days of filter usage user set date
                    if (daysToFilterChangeCounting <= 3 && dateAndTime.getDay() != xCh1) {
                        notificationCh1DaysLeft();
                        int xCh1 = dateAndTime.getDay();
                        mainPrefsEditor.putInt("xCh1", xCh1).apply();
                    }
                    // Every hour check if user consumed settled amount of water, if not, send notification
                    else if (howMuchToDrink > 0) {
                        if (dateAndTime.getTimeHour() != xCh2 && dateAndTime.getTimeMinute() == 0
                                && dateAndTime.getTimeSeconds() == 0) {
                            notificationCh2DrinkReminder();
                            int xCh2 = dateAndTime.getTimeHour();
                            mainPrefsEditor.putInt("xCh2", xCh2).apply();
                        }
                    }
                    // Check if filter still can filter some water based on it efficiency, send notifications
                    //  if filter have less than 10l of its efficiency until expiration.
                    else if (howMuchToFilterLeft <= 10 && dateAndTime.getTimeHour() != xCh3) {
                        notificationCh3FilterEfficiencyWater();
                        int xCh3 = dateAndTime.getTimeHour();
                        mainPrefsEditor.putInt("xCh3", xCh3).apply();
                    }
                    handler.postDelayed(this, delay);
                }
            }, delay);
        }
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
    private void notificationCh1DaysLeft() {
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

    private void notificationCh2DrinkReminder() {
        String title = "BottleApp";
        String message = "Don't forget to drink more water!";

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_2_ID)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .build();
        notificationManager.notify(2, notification);
    }

    private void notificationCh3FilterEfficiencyWater() {
        SharedPreferences mainPrefsReceiver = getApplicationContext().getSharedPreferences(
                "mainPrefs", Context.MODE_PRIVATE);
        SharedPreferences userProfilePrefsReceiver = getApplicationContext().getSharedPreferences(
                "userProfilePrefs", Context.MODE_PRIVATE);
        int filterEfficiency = userProfilePrefsReceiver.getInt("filterEfficiency", 0);
        int filterEfficiencyCounting = mainPrefsReceiver.getInt("filterEfficiencyCounting", 0);
        int howMuchToFilterLeft = filterEfficiency - (filterEfficiencyCounting / 1000);
        double filterEfficiencyCountingProjection = (filterEfficiency - (double) filterEfficiencyCounting / 1000);

        String title = "Filter";
        String message = null;

        switch (howMuchToFilterLeft) {
            case 10:
            case 5:
            case 3:
            case 2:
            case 1:
                message = "Have only: " + filterEfficiencyCountingProjection + "l left to change!";
                break;
            case 0:
                message = "Used up, change it today!";
                break;
        }

        if (howMuchToFilterLeft < 0) {
            message = "Used up " + filterEfficiencyCountingProjection + "l ago, change it today!";
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
