package com.matis8571.bottleapp;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import static com.matis8571.bottleapp.Notifications.CHANNEL_1_ID;
import static com.matis8571.bottleapp.Notifications.CHANNEL_2_ID;

public class MainActivity extends AppCompatActivity {
    //creates a tag variable to later tag activities in logs
    private static final String TAG = "MainActivity";

    TextView welcomeText, profileSetupText, showProfileText, mainTimeText, mainDateText, alarmNotificationsText;
    Button profileEditButton, showProfileButton, showProfileButtonToast, test;
    private int daysCounter, x, userChangeAfterDays;
    DateAndTime dateAndTime = new DateAndTime();
    protected static boolean enableShowProfileButton = false;
    private NotificationManagerCompat notificationManager;

    @SuppressLint("SetTextI18n")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity_screen);
        //Log.d tags a message to method, so it will pop up in a log screen every time we call this method
        // with custom text "Starting"
        Log.d(TAG, "onCreate: Starting");
        notificationManager = NotificationManagerCompat.from(this);
        check();

        //make new button/text object using previously setup id
        profileEditButton = (Button) findViewById(R.id.profileEditButton);
        showProfileButton = (Button) findViewById(R.id.showProfileButton);
        showProfileButtonToast = (Button) findViewById(R.id.showProfileButtonToast);
        test = (Button) findViewById(R.id.test);
        welcomeText = (TextView) findViewById(R.id.welcomeText);
        profileSetupText = (TextView) findViewById(R.id.profileSetupText);
        showProfileText = (TextView) findViewById(R.id.showProfileText);
        mainTimeText = (TextView) findViewById(R.id.time);
        mainDateText = (TextView) findViewById(R.id.date);
        alarmNotificationsText = (TextView) findViewById(R.id.alarmNotificationsText);

        //TODO set alarm messages
        alarmNotificationsText.setText("");
        welcomeText.setText("Welcome to your Bottle Application!");
        profileSetupText.setText("Edit profile:");
        showProfileText.setText("Show profile:");
        mainTimeText.setText(dateAndTime.getTime());
        mainDateText.setText(dateAndTime.getDate());

        SharedPreferences mainPrefs = getSharedPreferences("mainPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor mainPrefsEditor = mainPrefs.edit();
        mainPrefsEditor.putInt("daysCounter", daysCounter);
        mainPrefsEditor.apply();

        SharedPreferences filterPrefsReceiver = getApplicationContext().getSharedPreferences(
                "filterPrefs", Context.MODE_PRIVATE);
        userChangeAfterDays = filterPrefsReceiver.getInt("userChangeAfterDays", 0);

        //puts transparent button on top of inactive profile button
        // which is supposed to only show toast message and deactivate it
        // when profile button becomes active
        showProfileButton.setEnabled(false);
        if (enableShowProfileButton) {
            showProfileButtonToast.setEnabled(false);
            showProfileButton.setEnabled(true);
        }

        //make event when button does something when clicked
        profileEditButton.setOnClickListener(new View.OnClickListener() {
            @Override
            //on button click
            public void onClick(View view) {
                Log.d(TAG, "onClick: profileEditButton");
                Intent profileEditButtonIntent = new Intent(MainActivity.this, ProfileSetupScreenActivity.class);
                startActivity(profileEditButtonIntent);
            }
        });

        showProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (enableShowProfileButton) {
                    Log.d(TAG, "onClick: showProfileButton(active)");
                    Intent showProfileButtonIntent = new Intent(MainActivity.this, ProfileScreenActivity.class);
                    startActivity(showProfileButtonIntent);
                }
            }
        });

        showProfileButtonToast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: showProfileButton(inactive - toast)");
                //shows little pop up on screen with custom text
                Toast.makeText(MainActivity.this, "Profile not setup", Toast.LENGTH_SHORT).show();
            }
        });

        test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notificationCh1Days();
            }
        });
    }

    /**
     * Builds new notification pop up message with custom properties (Title, Test and Icon required)
     * on previously set channels. Then calls NotificationManagerCompat with .notify to call for a
     * notification pop up.
     */
    //sends notification about how many days left till previously setup (by user) days cap
    public void notificationCh1Days() {
        String title = "BottleApp";
        String message = (userChangeAfterDays - daysCounter) + " days left to filter change.";

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_1_ID)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .build();

        notificationManager.notify(1, notification);
    }

    public void notificationCh2Quantity() {
        String title = "title";
        String message = "message";

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_2_ID)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .build();

        notificationManager.notify(2, notification);
    }

    /**
     * Method called to initiate every Check Method at once
     */
    public void check() {
        countingDaysToMaxEfficiency();
//        countingWaterToMaxEfficiency();
    }

    /**
     * CM(Check Method) checks if current day equals X, if false adds 1 to daysCounter
     * and then is set to true to reset for today
     */
    private void countingDaysToMaxEfficiency() {
        if (x != dateAndTime.getDay()) {
            daysCounter = daysCounter + 1;
            x = dateAndTime.getDay();
        }
    }

}