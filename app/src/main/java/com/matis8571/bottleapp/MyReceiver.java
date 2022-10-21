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

        NotificationCompat.Builder builderCh1 = notificationUtils.notificationCh1DaysLeft(
                "Filter", textCh1);
        NotificationCompat.Builder builderCh2 = notificationUtils.notificationCh2DrinkReminder(
                "Drink water!", textCh2);
        NotificationCompat.Builder builderCh3 = notificationUtils.notificationCh3FilterEfficiencyWater(
                "Filter", textCh3);

        if (howMuchToDrink > 0) {
            notificationUtils.getManager().notify(2, builderCh2.build());
        }
    }
}