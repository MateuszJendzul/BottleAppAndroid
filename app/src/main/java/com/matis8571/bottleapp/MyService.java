package com.matis8571.bottleapp;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import java.util.Calendar;

public class MyService extends ContextWrapper {
    private static final String TAG = "MyService";
    public static final String CHANNEL_1_ID = "channel1";
    public static final String CHANNEL_2_ID = "channel2";
    public static final String CHANNEL_3_ID = "channel3";
    private NotificationManager notificationManager;
    private Context context;
    Calendar calendar = Calendar.getInstance();

    public MyService(Context base) {
        super(base);
        context = base;
        Log.d(TAG, "Starting");
        createNotificationChannels();
    }

    public void setReminder(Calendar setDate, Calendar dateNow) {
        Intent intent = new Intent(context, MyReceiver.class);
        @SuppressLint("UnspecifiedImmutableFlag") PendingIntent pendingIntent =
                PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        if (dateNow.after(setDate)) {
            Log.d(TAG, "Added a day");
            setDate.add(Calendar.DATE, 1);
        }
        alarmManager.set(AlarmManager.RTC_WAKEUP, setDate.getTimeInMillis(), pendingIntent);
        Log.d(TAG, "setReminderDailyInterval: " + setDate + " added notification at channel2");
    }

    public NotificationManager getManager() {
        if (notificationManager == null) {
            notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        }
        return notificationManager;
    }

    /**
     * Builds new notification message with custom properties (Title, Test and Icon required)
     * on previously set channels.
     */
    public NotificationCompat.Builder notificationCh1DaysLeft(String title, String text) {
        return new NotificationCompat.Builder(this, CHANNEL_1_ID)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(title)
                .setContentText(text)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE);
    }

    public NotificationCompat.Builder notificationCh2DrinkReminder(String title, String text) {
        return new NotificationCompat.Builder(this, CHANNEL_2_ID)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(title)
                .setContentText(text)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE);
    }

    public NotificationCompat.Builder notificationCh3FilterEfficiencyWater(String title, String text) {
        return new NotificationCompat.Builder(this, CHANNEL_3_ID)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(title)
                .setContentText(text)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE);
    }

    /**
     * Use to create new channels for notifications.
     * Checks if current android version allows creation of notification channels
     * (not available under Oreo android version), then sets channel: id, name, and notification
     * importance settings along with description, and lastly creates channels with set properties.
     */
    private void createNotificationChannels() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel1 = new NotificationChannel(
                    CHANNEL_1_ID,
                    "Days to filter change",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            channel1.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            channel1.setDescription("Send notifications for the last 3 days of filter usage user set date");

            NotificationChannel channel2 = new NotificationChannel(
                    CHANNEL_2_ID,
                    "Daily water drink",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            channel2.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            channel2.setDescription("Every hour check if user consumed settled amount of water " +
                    "if not, send notification");

            NotificationChannel channel3 = new NotificationChannel(
                    CHANNEL_3_ID,
                    "Remaining filter efficiency",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            channel3.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            channel3.setDescription("Send notifications if filter have less than 10l of its efficiency " +
                    "until expiration");
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            getManager().createNotificationChannel(channel1);
            getManager().createNotificationChannel(channel2);
            getManager().createNotificationChannel(channel3);
        }
    }

//    private int getTimeHours(){
//        return calendar.get(Calendar.HOUR_OF_DAY);
//    }
//
//    private int getTimeMinutes(){
//        return calendar.get(Calendar.MINUTE);
//    }
//
//    private int getTimeSeconds (){
//        return calendar.get(Calendar.SECOND);
//    }
//
//    private int getDay(){
//        return calendar.get(Calendar.DAY_OF_MONTH);
//    }
//
//    private int getMonth(){
//        return calendar.get(Calendar.MONTH) + 1;
//    }
//
//    private int getYear(){
//        return calendar.get(Calendar.YEAR);
//    }

}