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
//TODO fix bottle drunk button so it wont show negative amounts.
// check how to use methods when application is closed
// update rest of the classes (fonts)
public class MainActivity extends AppCompatActivity {
    //creates a tag variable to later tag activities in logs
    private static final String TAG = "MainActivity";

    TextView welcomeText, profileSetupText, showProfileText, mainTimeText, mainDateText, daysToChangeFilterText,
            showInMainDailyWaterConsumptionText;
    Button profileEditButton, showProfileButton, bottleDrunkButton, showProfileButtonToast;
    protected static boolean enableShowProfileButton = false;
    private int daysCounter, x, bottlesDrunk, howMuchToDrink;
    private NotificationManagerCompat notificationManager;
    DateAndTime dateAndTime = new DateAndTime();

    @SuppressLint("SetTextI18n")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity_screen);
        //Log.d tags a message to method, so it will pop up in a log screen every time we call this method
        // with custom text "Starting"
        Log.d(TAG, "onCreate: Starting");
        mainCheckMethod();
        howMuchToDrink();
        notificationManager = NotificationManagerCompat.from(this);

        SharedPreferences filterPrefsReceiver = getApplicationContext().getSharedPreferences(
                "filterPrefs", Context.MODE_PRIVATE);
        int userChangeAfterDays = filterPrefsReceiver.getInt("userChangeAfterDays", 0);

        //make new button/text object using previously setup id
        profileEditButton = (Button) findViewById(R.id.profileEditButton);
        showProfileButton = (Button) findViewById(R.id.showProfileButton);
        bottleDrunkButton = (Button) findViewById(R.id.bottleDrunkButton);
        showProfileButtonToast = (Button) findViewById(R.id.showProfileButtonToast);
        welcomeText = (TextView) findViewById(R.id.welcomeText);
        profileSetupText = (TextView) findViewById(R.id.profileSetupText);
        showProfileText = (TextView) findViewById(R.id.showProfileText);
        mainTimeText = (TextView) findViewById(R.id.time);
        mainDateText = (TextView) findViewById(R.id.date);
        daysToChangeFilterText = (TextView) findViewById(R.id.daysToChangeFilter);
        showInMainDailyWaterConsumptionText = (TextView) findViewById(R.id.showInMainDailyWaterConsumption);

        showInMainDailyWaterConsumptionText.setText("Water to drink: " + howMuchToDrink + " ml");
        daysToChangeFilterText.setText("Days left to filter change: " + (userChangeAfterDays - daysCounter));
        welcomeText.setText("Welcome to your Bottle Application!");
        profileSetupText.setText("Edit profile:");
        showProfileText.setText("Show profile:");
        mainTimeText.setText(dateAndTime.getTime());
        mainDateText.setText(dateAndTime.getDate());

        SharedPreferences mainPrefs = getSharedPreferences("mainPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor mainPrefsEditor = mainPrefs.edit();
        mainPrefsEditor.putInt("daysCounter", daysCounter);
        mainPrefsEditor.apply();

        //puts transparent button on top of inactive profile button
        // which is supposed to only show toast message and deactivate it
        // when profile button becomes active
        showProfileButton.setEnabled(false);
        if (enableShowProfileButton) {
            showProfileButtonToast.setEnabled(false);
            showProfileButton.setEnabled(true);
        }

        //sends notifications every day for the last 3 days of
        if (daysCounter == (userChangeAfterDays - 3) || daysCounter == (userChangeAfterDays - 2) ||
                daysCounter == (userChangeAfterDays - 1)) {
            notificationCh1Days();
        }

        //checks if user didn't yet drink settled amount of water and then sends notifications at fixed hours
        if (howMuchToDrink > 0) {
            switch (dateAndTime.getTimeHour()) {
                case 10:
                case 14:
                case 18:
                    notificationCh2DrinkReminder();
                    break;
            }
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

        bottleDrunkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addOneToBottlesDrunk();
                howMuchToDrink();
                showInMainDailyWaterConsumptionText.setText("Water to drink: " + howMuchToDrink + " ml");
                Toast.makeText(MainActivity.this, "You drunk another water bottle!", Toast.LENGTH_SHORT).show();
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
        SharedPreferences filterPrefsReceiver = getApplicationContext().getSharedPreferences(
                "filterPrefs", Context.MODE_PRIVATE);
        int userChangeAfterDays = filterPrefsReceiver.getInt("userChangeAfterDays", 0);

        String notificationCh1Title = "BottleApp";
        String notificationCh1Message;
        if ((userChangeAfterDays - daysCounter) == 0) {
            notificationCh1Message = "Change filter now!";
        } else {
            notificationCh1Message = "Days left to filter change: " + (userChangeAfterDays - daysCounter);
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

    /**
     * Method called to initiate every Check Method once a day. CMs checks if current day equals X,
     * if false it executes its contents and then sets X as current day to reset it for today.
     */
    public void mainCheckMethod() {
        if (x != dateAndTime.getDay()) {
            countingDaysToFilterChange();
            x = dateAndTime.getDay();
        }
    }

    //CM(Check Method) adds 1 to daysCounter
    private void countingDaysToFilterChange() {
        daysCounter = daysCounter + 1;
    }

    private void addOneToBottlesDrunk() {
        bottlesDrunk = bottlesDrunk + 1;
    }

    //counts on the base of previously settled properties how much more water does user need to drink today
    @SuppressLint("SetTextI18n")
    private void howMuchToDrink() {
        SharedPreferences filterPrefsReceiver = getApplicationContext().getSharedPreferences(
                "filterPrefs", Context.MODE_PRIVATE);
        int dailyWaterConsumption = filterPrefsReceiver.getInt("dailyWaterConsumption", 0);

        SharedPreferences userProfilePrefsReceiver = getApplicationContext().getSharedPreferences(
                "userProfilePrefs", Context.MODE_PRIVATE);
        int bottleCapacity = userProfilePrefsReceiver.getInt("bottleCapacity", 0);

        howMuchToDrink = dailyWaterConsumption - (bottlesDrunk * bottleCapacity);
    }
}