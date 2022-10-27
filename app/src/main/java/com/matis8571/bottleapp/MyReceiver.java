package com.matis8571.bottleapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.core.app.NotificationCompat;

public class MyReceiver extends BroadcastReceiver {
    private static final String TAG = "MyReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationUtils notificationUtils = new NotificationUtils(context);
        Log.d(TAG, "onReceive: Starting");

        SharedPreferences mainPrefsReceiver = context.getSharedPreferences(
                "mainPrefs", Context.MODE_PRIVATE);
        SharedPreferences userProfilePrefsReceiver = context.getSharedPreferences(
                "userProfilePrefs", Context.MODE_PRIVATE);
        SharedPreferences mainPrefs = context.getSharedPreferences("mainPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor mainPrefsEditor = mainPrefs.edit();
        int xChannel1 = mainPrefsReceiver.getInt("xChannel1", 3);
        int daysToFilterChangeCounting = mainPrefsReceiver.getInt("daysToFilterChangeCounting", 0);
        int howMuchToDrink = mainPrefsReceiver.getInt("howMuchToDrink", 0);
        int countDaysToFilterChange = mainPrefsReceiver.getInt("countDaysToFilterChange", 0);
        int filterEfficiency = userProfilePrefsReceiver.getInt("filterEfficiency", 0);
        int filterEfficiencyCounting = mainPrefsReceiver.getInt("filterEfficiencyCounting", 0);
        int howMuchToFilterLeft = filterEfficiency - (filterEfficiencyCounting / 1000);
        double filterEfficiencyCountingProjection = (filterEfficiency - (double) filterEfficiencyCounting / 1000);
        String textCh1 = "Days left to filter change: " + countDaysToFilterChange;
        String textCh2 = "Don't forget to drink more water!";
        String textCh3 = null;

        switch (howMuchToFilterLeft) {
            case 10:
            case 5:
            case 3:
            case 2:
            case 1:
                textCh3 = "Have only: " + filterEfficiencyCountingProjection + "l left to change!";
                break;
            case 0:
                textCh3 = "Used up, change it today!";
                break;
        }

        if (howMuchToFilterLeft < 0) {
            textCh3 = "Used up " + filterEfficiencyCountingProjection + "l ago, change it today!";
        }

        if (xChannel1 == daysToFilterChangeCounting) {
            Log.d(TAG, "ifPassed: reminderNotificationChannel1");
            NotificationCompat.Builder builderCh1 = notificationUtils.notificationCh1DaysLeft(
                    "Filter", textCh1);
            notificationUtils.getManager().notify(1, builderCh1.build());
            xChannel1--;
            mainPrefsEditor.putInt("xChannel1", xChannel1).apply();

        }

        if (howMuchToDrink > 0) {
            Log.d(TAG, "ifPassed: reminderNotificationChannel2");
            NotificationCompat.Builder builderCh2 = notificationUtils.notificationCh2DrinkReminder(
                    "Drink water!", textCh2);
            notificationUtils.getManager().notify(2, builderCh2.build());

        }

        if (howMuchToFilterLeft <= 10) {
            Log.d(TAG, "ifPassed: reminderNotificationChannel3");
            NotificationCompat.Builder builderCh3 = notificationUtils.notificationCh3FilterEfficiencyWater(
                    "Filter", textCh3);
            notificationUtils.getManager().notify(3, builderCh3.build());
        }
    }
}