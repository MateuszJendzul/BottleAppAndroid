package com.matis8571.bottleapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import androidx.core.app.NotificationCompat;

public class ReminderBroadcast extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        MyService myService = new MyService(context);
        NotificationCompat.Builder builder = myService.notificationCh2DrinkReminder(
                "BottleApp", "Don't forget to drink more water!");

        SharedPreferences mainPrefsReceiver = context.getSharedPreferences(
                "mainPrefs", Context.MODE_PRIVATE);
        int howMuchToDrink = mainPrefsReceiver.getInt("howMuchToDrink", 0);

        if (howMuchToDrink > 0) {
            myService.getManager().notify(2, builder.build());
        }
    }
}
