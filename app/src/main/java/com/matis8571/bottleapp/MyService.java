package com.matis8571.bottleapp;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.util.Calendar;

public class MyService extends Service {
    private static final String TAG = "MyService";
    private final int delay = 5000;
    public static final String CHANNEL_1_ID = "channel1";
    public static final String CHANNEL_2_ID = "channel2";
    public static final String CHANNEL_3_ID = "channel3";
    private int xChannel1, xChannel3, day, month, year, timeSecond, timeMinute, timeHour;
    private NotificationManagerCompat notificationManager;
    private final Handler handler = new Handler();

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate: Starting");
        createNotificationChannels();
        notificationManager = NotificationManagerCompat.from(this);
        SharedPreferences mainPrefsReceiver = getApplicationContext().getSharedPreferences(
                "mainPrefs", Context.MODE_PRIVATE);
        SharedPreferences userProfilePrefsReceiver = getApplicationContext().getSharedPreferences(
                "userProfilePrefs", Context.MODE_PRIVATE);
        SharedPreferences filterPrefsReceiver = getApplicationContext().getSharedPreferences(
                "filterPrefs", Context.MODE_PRIVATE);
        SharedPreferences myServicePrefsReceiver = getApplicationContext().getSharedPreferences(
                "myServicePrefs", Context.MODE_PRIVATE);

        boolean enableShowProfileButton = filterPrefsReceiver.getBoolean("enableShowProfileButton", false);
        int daysToFilterChangeCounting = mainPrefsReceiver.getInt("daysToFilterChangeCounting", 0);
        int howMuchToDrink = mainPrefsReceiver.getInt("howMuchToDrink", 0);
        int filterEfficiencyCounting = mainPrefsReceiver.getInt("filterEfficiencyCounting", 0);
        int filterEfficiency = userProfilePrefsReceiver.getInt("filterEfficiency", 0);
        int howMuchToFilterLeft = filterEfficiency - (filterEfficiencyCounting / 1000);
        SharedPreferences myServicePrefs = getSharedPreferences("myServicePrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor myServicePrefsEditor = myServicePrefs.edit();
        myServicePrefsEditor.putInt("howMuchToFilterLeft", howMuchToFilterLeft).apply();
        xChannel1 = myServicePrefsReceiver.getInt("xChannel1", 3);
        xChannel3 = myServicePrefsReceiver.getInt("xChannel3", 10);

        if (enableShowProfileButton) {
            // Refresh containing code every value (seconds) defined by delay variable
            handler.postDelayed(new Runnable() {
                public void run() { //code to run below
                    // Get current date and time
                    Calendar calendar = Calendar.getInstance();
                    day = calendar.get(Calendar.DAY_OF_MONTH);
                    month = calendar.get(Calendar.MONTH) + 1;
                    year = calendar.get(Calendar.YEAR);
                    timeHour = calendar.get(Calendar.HOUR_OF_DAY);
                    timeMinute = calendar.get(Calendar.MINUTE);
                    timeSecond = calendar.get(Calendar.SECOND);

                    // Send notifications for the last 3 days of filter usage user set date
                    if (xChannel1 == daysToFilterChangeCounting && timeHour == 18) {
                        notificationCh1DaysLeft();
                        xChannel1--;
                        myServicePrefsEditor.putInt("xChannel1", xChannel1).apply();
                    }
                    // Every hour check if user consumed settled amount of water, if not, send notification
                    if (howMuchToDrink > 0) {
                        if(timeMinute == 0 && (timeSecond == 0 || timeSecond == 1 ||
                                timeSecond == 2 || timeSecond == 3 || timeSecond == 4 )) {
                            switch (timeHour) {
                                case 10:
                                case 12:
                                case 14:
                                case 16:
                                case 18:
                                    notificationCh2DrinkReminder();
                                    break;
                            }
                        }
                    }
                    // Check if filter still can filter some water based on it efficiency, send notifications
                    //  if filter have less than 10l of its efficiency until expiration.
                    if (xChannel3 == howMuchToFilterLeft) {
                        notificationCh3FilterEfficiencyWater();
                        xChannel3 = xChannel3 - 2;
                        myServicePrefsEditor.putInt("xChannel3", xChannel3).apply();
                    }
                    handler.postDelayed(this, delay);
                }
            }, delay);
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public int getDay() {
        return day;
    }

    public int getMonth() {
        return month;
    }

    public int getYear() {
        return year;
    }

    public int getTimeMinute() {
        return timeMinute;
    }

    public int getTimeHour() {
        return timeHour;
    }

    public int getTimeSeconds() {
        return timeSecond;
    }

    /**
     * Return actual time of the day using java.util.Calendar.
     */
    public String getTime() {
        if (timeMinute < 10) {
            return timeHour + ":0" + timeMinute;
        } else {
            return timeHour + ":" + timeMinute;
        }
    }

    /**
     * Return actual date using java.util.Calendar.
     * If number representing month is one digital, adds 0 to make it two-digit.
     */
    public String getDate() {
        if (getMonth() < 9) {
            return getDay() + ".0" + getMonth() + "." + getYear();
        } else {
            return getDay() + "." + getMonth() + "." + getYear();
        }
    }

    /**
     * Change to new variable at 23:59.
     *
     * @param toReset target value to reset
     * @param resetTo set new value after reset
     * @return returns new value
     */
    @SuppressWarnings({"ParameterCanBeLocal", "UnusedAssignment"})
    public int dailyReset(int toReset, int resetTo) {
        if (timeHour == 23 && timeMinute == 59) {
            toReset = resetTo;
        }
        return resetTo;
    }

    /**
     * At 23:59 return target boolean as false, otherwise return true.
     *
     * @param toReset target boolean to change
     * @return returns false at 23:59
     */
    public boolean dailyReset(boolean toReset) {
        if (timeHour == 23 && timeMinute == 59) {
            return !toReset;
        } else {
            return toReset;
        }
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
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel1.setDescription("Send notifications for the last 3 days of filter usage user set date");

            NotificationChannel channel2 = new NotificationChannel(
                    CHANNEL_2_ID,
                    "Daily water drink",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel2.setDescription("Every hour check if user consumed settled amount of water " +
                    "if not, send notification");

            NotificationChannel channel3 = new NotificationChannel(
                    CHANNEL_3_ID,
                    "Remaining filter efficiency",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel3.setDescription("Send notifications if filter have less than 10l of its efficiency " +
                    "until expiration");
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel1);
            notificationManager.createNotificationChannel(channel2);
            notificationManager.createNotificationChannel(channel3);
        }
    }
}