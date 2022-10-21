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
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import java.util.Calendar;

public class NotificationUtils extends ContextWrapper {
    private static final String TAG = "MyService";
    public static final String CHANNEL_1_ID = "channel1";
    public static final String CHANNEL_2_ID = "channel2";
    public static final String CHANNEL_3_ID = "channel3";
    private NotificationManager notificationManager;
    private Context context;

    public NotificationUtils(Context base) {
        super(base);
        context = base;
        Log.d(TAG, "Starting");
        createNotificationChannels();
    }

    public void setReminderCh1(Calendar setDate) {
        Intent intent = new Intent(context, MyReceiver.class);
        @SuppressLint("UnspecifiedImmutableFlag") PendingIntent pendingIntent =
                PendingIntent.getBroadcast(context, 1, intent, 0);
        AlarmManager alarmManagerCh1 = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmManagerCh1.set(AlarmManager.RTC_WAKEUP, setDate.getTimeInMillis(), pendingIntent);
        Log.d(TAG, "setReminder: " + setDate + " added notification");
    }

    @SuppressLint("UnspecifiedImmutableFlag")
    public void setReminderCh2(Calendar setDate1, Calendar setDate2, Calendar setDate3,
                               Calendar setDate4, Calendar setDate5) {
        Intent intent = new Intent(context, MyReceiver.class);
        PendingIntent pendingIntentCh2_1 = PendingIntent.getBroadcast(context, 2_1, intent, 0);
        PendingIntent pendingIntentCh2_2 = PendingIntent.getBroadcast(context, 2_2, intent, 0);
        PendingIntent pendingIntentCh2_3 = PendingIntent.getBroadcast(context, 2_3, intent, 0);
        PendingIntent pendingIntentCh2_4 = PendingIntent.getBroadcast(context, 2_4, intent, 0);
        PendingIntent pendingIntentCh2_5 = PendingIntent.getBroadcast(context, 2_5, intent, 0);
        AlarmManager alarmManagerCh2_1 = (AlarmManager) getSystemService(ALARM_SERVICE);
        AlarmManager alarmManagerCh2_2 = (AlarmManager) getSystemService(ALARM_SERVICE);
        AlarmManager alarmManagerCh2_3 = (AlarmManager) getSystemService(ALARM_SERVICE);
        AlarmManager alarmManagerCh2_4 = (AlarmManager) getSystemService(ALARM_SERVICE);
        AlarmManager alarmManagerCh2_5 = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmManagerCh2_1.set(AlarmManager.RTC_WAKEUP, setDate1.getTimeInMillis(), pendingIntentCh2_1);
        alarmManagerCh2_2.set(AlarmManager.RTC_WAKEUP, setDate2.getTimeInMillis(), pendingIntentCh2_2);
        alarmManagerCh2_3.set(AlarmManager.RTC_WAKEUP, setDate3.getTimeInMillis(), pendingIntentCh2_3);
        alarmManagerCh2_4.set(AlarmManager.RTC_WAKEUP, setDate4.getTimeInMillis(), pendingIntentCh2_4);
        alarmManagerCh2_5.set(AlarmManager.RTC_WAKEUP, setDate5.getTimeInMillis(), pendingIntentCh2_5);
        Log.d(TAG, "setReminder:\n" + setDate1 + " added notification\n" + setDate2 +
                " added notification\n" + setDate3 + " added notification\n" + setDate4 +
                " added notification\n" + setDate5 + " added notification");
    }

    public void setReminderCh3(Calendar setDate) {
        Intent intent = new Intent(context, MyReceiver.class);
        @SuppressLint("UnspecifiedImmutableFlag") PendingIntent pendingIntent =
                PendingIntent.getBroadcast(context, 3, intent, 0);
        AlarmManager alarmManagerCh3 = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmManagerCh3.set(AlarmManager.RTC_WAKEUP, setDate.getTimeInMillis(), pendingIntent);
        Log.d(TAG, "setReminder: " + setDate + " added notification");
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
}