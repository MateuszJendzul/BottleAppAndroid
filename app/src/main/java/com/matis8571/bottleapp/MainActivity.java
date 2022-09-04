package com.matis8571.bottleapp;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
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

    TextView welcomeText, profileSetupText, showProfileText, daysToChangeFilterText,
            showInMainDailyWaterConsumptionText, showInMainWaterDrunkText;
    Button profileEditButton, showProfileButton, addBottleButton, removeBottleButton, showProfileButtonToast,
            removeBottleButtonToast, addBottleButtonToast;
    private int howMuchToDrink, waterToday;
    private NotificationManagerCompat notificationManager;
    DateAndTime dateAndTime = new DateAndTime();

    @SuppressLint("SetTextI18n")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity_screen);
        //Log.d tags a message to method, so it will pop up in a log screen every time we call this method
        // with custom text "Starting"
        Log.d(TAG, "onCreate: Starting");
        notificationManager = NotificationManagerCompat.from(this);
        dailyPropertiesReset();
        howMuchToDrink();
        countToFilterEfficiency();

        SharedPreferences filterPrefsReceiver = getApplicationContext().getSharedPreferences(
                "filterPrefs", Context.MODE_PRIVATE);
        SharedPreferences userProfilePrefsReceiver = getApplicationContext().getSharedPreferences(
                "userProfilePrefs", Context.MODE_PRIVATE);
        SharedPreferences mainPrefsReceiver = getApplicationContext().getSharedPreferences(
                "mainPrefs", Context.MODE_PRIVATE);

        int userChangeAfterDays = filterPrefsReceiver.getInt("userChangeAfterDays", 0);
        boolean enableShowProfileButton = filterPrefsReceiver.getBoolean("enableShowProfileButton", false);
        int bottleCapacity = userProfilePrefsReceiver.getInt("bottleCapacity", 0);
        howMuchToDrink = mainPrefsReceiver.getInt("howMuchToDrink", 0);
        waterToday = mainPrefsReceiver.getInt("waterToday", 0);
        int daysCounter = mainPrefsReceiver.getInt("daysCounter", 0);

        //make new button/text object using previously setup id
        profileEditButton = findViewById(R.id.profileEditButton);
        showProfileButton = findViewById(R.id.showProfileButton);
        addBottleButton = findViewById(R.id.addBottle);
        removeBottleButton = findViewById(R.id.removeBottle);
        showProfileButtonToast = findViewById(R.id.showProfileButtonToast);
        removeBottleButtonToast = findViewById(R.id.removeBottleToast);
        addBottleButtonToast = findViewById(R.id.addBottleToast);
        welcomeText = findViewById(R.id.welcomeText);
        profileSetupText = findViewById(R.id.profileSetupText);
        showProfileText = findViewById(R.id.showProfileText);
        daysToChangeFilterText = findViewById(R.id.daysToChangeFilter);
        showInMainDailyWaterConsumptionText = findViewById(R.id.showInMainDailyWaterConsumption);
        showInMainWaterDrunkText = findViewById(R.id.showInMainWaterDrunk);

        if (howMuchToDrink <= 0) {
            showInMainDailyWaterConsumptionText.setText("Water to drink: " + 0 + " ml");
        } else {
            showInMainDailyWaterConsumptionText.setText("Water to drink: " + howMuchToDrink + " ml");
        }
        showInMainWaterDrunkText.setText("Today: " + waterToday + "ml of water");
        daysToChangeFilterText.setText("Days left to filter change: " + (userChangeAfterDays - daysCounter));
        welcomeText.setText("Welcome to your Bottle Application!");
        profileSetupText.setText("Edit profile:");
        showProfileText.setText("Show profile:");

        //puts transparent button on top of inactive profile button
        // which is supposed to only show toast message and deactivate it
        // when profile button becomes active
        showProfileButton.setEnabled(false);
        addBottleButton.setEnabled(false);
        removeBottleButton.setEnabled(false);
        if (enableShowProfileButton) {
            addBottleButtonToast.setEnabled(false);
            removeBottleButtonToast.setEnabled(false);
            showProfileButtonToast.setEnabled(false);
            addBottleButton.setEnabled(true);
            removeBottleButton.setEnabled(true);
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
        //on button click
        profileEditButton.setOnClickListener(view -> {
            Log.d(TAG, "onClick: profileEditButton");
            Intent profileEditButtonIntent = new Intent(MainActivity.this, ProfileSetupScreenActivity.class);
            startActivity(profileEditButtonIntent);
        });

        showProfileButton.setOnClickListener(view -> {
            if (enableShowProfileButton) {
                Log.d(TAG, "onClick: showProfileButton(active)");
                Intent showProfileButtonIntent = new Intent(MainActivity.this, ProfileScreenActivity.class);
                startActivity(showProfileButtonIntent);
            }
        });

        showProfileButtonToast.setOnClickListener(v -> {
            Log.d(TAG, "onClick: showProfileButton(inactive - toast)");
            //shows little pop up on screen with custom text
            Toast.makeText(MainActivity.this, "Profile not setup", Toast.LENGTH_SHORT).show();
        });

        addBottleButtonToast.setOnClickListener(v -> {
            Log.d(TAG, "onClick: addBottleButton(inactive - toast");
            Toast.makeText(MainActivity.this, "Profile not setup", Toast.LENGTH_SHORT).show();
        });

        removeBottleButtonToast.setOnClickListener(v -> {
            Log.d(TAG, "onClick: removeBottleButton(inactive - toast");
            Toast.makeText(MainActivity.this, "Profile not setup", Toast.LENGTH_SHORT).show();
        });

        //checks if there is still any amount of water left and then
        addBottleButton.setOnClickListener(v -> {
            if (howMuchToDrink > 0 && howMuchToDrink >= bottleCapacity) {
                addBottlesDone();
            } else if (bottleCapacity > howMuchToDrink) {
                howMuchToDrink = 0;
                addBottlesDone();
            } else {
                addBottlesDoneExtended();
            }
            howMuchToDrink();
            howMuchToDrink = mainPrefsReceiver.getInt("howMuchToDrink", 0);
            waterToday = mainPrefsReceiver.getInt("waterToday", 0);
            if (howMuchToDrink <= 0) {
                showInMainDailyWaterConsumptionText.setText("Water to drink: " + 0 + " ml");
            } else {
                showInMainDailyWaterConsumptionText.setText("Water to drink: " + howMuchToDrink + " ml");
            }
            showInMainWaterDrunkText.setText("Today: " + waterToday + "ml");
        });

        removeBottleButton.setOnClickListener(view -> {
            if (howMuchToDrink >= bottleCapacity) {
                removeBottlesDone();
            } else if (howMuchToDrink < bottleCapacity) {
                howMuchToDrink = bottleCapacity;
                removeBottlesDone();
            } else {
                removeBottlesDoneExtended();
            }
            howMuchToDrink();
            howMuchToDrink = mainPrefsReceiver.getInt("howMuchToDrink", 0);
            waterToday = mainPrefsReceiver.getInt("waterToday", 0);
            if (howMuchToDrink <= 0) {
                showInMainDailyWaterConsumptionText.setText("Water to drink: " + 0 + " ml");
            } else {
                showInMainDailyWaterConsumptionText.setText("Water to drink: " + howMuchToDrink + " ml");
            }
            showInMainWaterDrunkText.setText("Today: " + waterToday + "ml");
        });
    }

    /**
     * Builds new notification message with custom properties (Title, Test and Icon required)
     * on previously set channels. Then calls NotificationManagerCompat with .notify to call for a
     * notification to show on phone screen.
     */
    //sends notification about how many days left till previously setup (by user) days cap
    public void notificationCh1Days() {
        SharedPreferences mainPrefsReceiver = getApplicationContext().getSharedPreferences(
                "mainPrefs", Context.MODE_PRIVATE);
        SharedPreferences filterPrefsReceiver = getApplicationContext().getSharedPreferences(
                "filterPrefs", Context.MODE_PRIVATE);
        int userChangeAfterDays = filterPrefsReceiver.getInt("userChangeAfterDays", 0);
        int daysCounter = mainPrefsReceiver.getInt("daysCounter", 0);

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
    public void dailyPropertiesReset() {
        SharedPreferences mainPrefsReceiver = getApplicationContext().getSharedPreferences(
                "mainPrefs", Context.MODE_PRIVATE);
        SharedPreferences filterPrefsReceiver = getApplicationContext().getSharedPreferences(
                "filterPrefs", Context.MODE_PRIVATE);
        SharedPreferences mainPrefs = getSharedPreferences("mainPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor mainPrefsEditor = mainPrefs.edit();

        int x = mainPrefsReceiver.getInt("x", dateAndTime.getDay());
        if (x != dateAndTime.getDay()) {
            countDaysToFilterChange();
            mainPrefsReceiver.edit().remove("bottlesDone").apply();
            mainPrefsReceiver.edit().remove("bottlesDoneExtend").apply();
            mainPrefsReceiver.edit().remove("waterToday").apply();
            x = dateAndTime.getDay();
            int howMuchToDrink = filterPrefsReceiver.getInt("dailyWaterConsumptionOnlyRead", 0);
            mainPrefsEditor.putInt("x", x).apply();
            mainPrefsEditor.putInt("howMuchToDrink", howMuchToDrink).apply();
        }
    }

    private void countToFilterEfficiency() {
        SharedPreferences userProfilePrefsReceiver = getApplicationContext().getSharedPreferences(
                "userProfilePrefs", Context.MODE_PRIVATE);
        int filterEfficiency = userProfilePrefsReceiver.getInt("filterEfficiency", 0);
        int bottleCapacity = userProfilePrefsReceiver.getInt("bottleCapacity", 0);
        SharedPreferences mainPrefsReceiver = getApplicationContext().getSharedPreferences(
                "mainPrefs", Context.MODE_PRIVATE);
        int bottlesDoneToFilterEfficiency = mainPrefsReceiver.getInt("bottlesDoneToFilterEfficiency", 0);
        int bottlesDoneExtendedToFilterEfficiency = mainPrefsReceiver.getInt("bottlesDoneExtendedToFilterEfficiency", 0);
        int countFilterEfficiency =
                (filterEfficiency * 1000) - ((bottlesDoneToFilterEfficiency + bottlesDoneExtendedToFilterEfficiency) * bottleCapacity);
        SharedPreferences mainPrefs = getSharedPreferences("mainPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor mainPrefsEditor = mainPrefs.edit();
        mainPrefsEditor.putInt("countFilterEfficiency", countFilterEfficiency).apply();
    }

    private void countDaysToFilterChange() {
        SharedPreferences mainPrefsReceiver = getApplicationContext().getSharedPreferences(
                "mainPrefs", Context.MODE_PRIVATE);
        SharedPreferences mainPrefs = getSharedPreferences("mainPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor mainPrefsEditor = mainPrefs.edit();
        int daysCounter = mainPrefsReceiver.getInt("daysCounter", 0);
        daysCounter++;
        mainPrefsEditor.putInt("daysCounter", daysCounter).apply();
    }

    private void addBottlesDone() {
        SharedPreferences mainPrefsReceiver = getApplicationContext().getSharedPreferences(
                "mainPrefs", Context.MODE_PRIVATE);
        SharedPreferences mainPrefs = getSharedPreferences("mainPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor mainPrefsEditor = mainPrefs.edit();
        int bottlesDone = mainPrefsReceiver.getInt("bottlesDone", 0);
        int bottlesDoneToFilterEfficiency = mainPrefsReceiver.getInt("bottlesDoneToFilterEfficiency", 0);
        bottlesDone++;
        bottlesDoneToFilterEfficiency++;
        mainPrefsEditor.putInt("bottlesDone", bottlesDone).apply();
        mainPrefsEditor.putInt("bottlesDoneToFilterEfficiency", bottlesDoneToFilterEfficiency).apply();
    }

    private void removeBottlesDone() {
        SharedPreferences mainPrefsReceiver = getApplicationContext().getSharedPreferences(
                "mainPrefs", Context.MODE_PRIVATE);
        SharedPreferences mainPrefs = getSharedPreferences("mainPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor mainPrefsEditor = mainPrefs.edit();
        int bottlesDone = mainPrefsReceiver.getInt("bottlesDone", 0);
        int bottlesDoneToFilterEfficiency = mainPrefsReceiver.getInt("bottlesDoneToFilterEfficiency", 0);
        bottlesDone--;
        bottlesDoneToFilterEfficiency--;
        mainPrefsEditor.putInt("bottlesDone", bottlesDone).apply();
        mainPrefsEditor.putInt("bottlesDoneToFilterEfficiency", bottlesDoneToFilterEfficiency).apply();
    }

    private void addBottlesDoneExtended() {
        SharedPreferences mainPrefsReceiver = getApplicationContext().getSharedPreferences(
                "mainPrefs", Context.MODE_PRIVATE);
        SharedPreferences mainPrefs = getSharedPreferences("mainPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor mainPrefsEditor = mainPrefs.edit();
        int bottlesDoneExtended = mainPrefsReceiver.getInt("bottlesDoneExtended", 0);
        int bottlesDoneExtendedToFilterEfficiency = mainPrefsReceiver.getInt(
                "bottlesDoneExtendedToFilterEfficiency", 0);
        bottlesDoneExtended++;
        bottlesDoneExtendedToFilterEfficiency++;
        mainPrefsEditor.putInt("bottlesDoneExtended", bottlesDoneExtended).apply();
        mainPrefsEditor.putInt("bottlesDoneExtendedToFilterEfficiency", bottlesDoneExtendedToFilterEfficiency).apply();
    }

    private void removeBottlesDoneExtended() {
        SharedPreferences mainPrefsReceiver = getApplicationContext().getSharedPreferences(
                "mainPrefs", Context.MODE_PRIVATE);
        SharedPreferences mainPrefs = getSharedPreferences("mainPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor mainPrefsEditor = mainPrefs.edit();
        int bottlesDoneExtended = mainPrefsReceiver.getInt("bottlesDoneExtended", 0);
        int bottlesDoneExtendedToFilterEfficiency = mainPrefsReceiver.getInt(
                "bottlesDoneExtendedToFilterEfficiency", 0);
        bottlesDoneExtended--;
        bottlesDoneExtendedToFilterEfficiency--;
        mainPrefsEditor.putInt("bottlesDoneExtended", bottlesDoneExtended).apply();
        mainPrefsEditor.putInt("bottlesDoneExtendedToFilterEfficiency", bottlesDoneExtendedToFilterEfficiency).apply();
    }

    //counts on the base of previously settled properties how much more water does user need to drink today
    //to prevent values from resetting after closing app, update them in SharedPreferences
    @SuppressLint({"SetTextI18n", "ApplySharedPref"})
    private void howMuchToDrink() {
        SharedPreferences mainPrefsReceiver = getApplicationContext().getSharedPreferences(
                "mainPrefs", Context.MODE_PRIVATE);
        SharedPreferences filterPrefsReceiver = getApplicationContext().getSharedPreferences(
                "filterPrefs", Context.MODE_PRIVATE);
        SharedPreferences userProfilePrefsReceiver = getApplicationContext().getSharedPreferences(
                "userProfilePrefs", Context.MODE_PRIVATE);
        SharedPreferences mainPrefs = getSharedPreferences("mainPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor mainPrefsEditor = mainPrefs.edit();
        int dailyWaterConsumption = filterPrefsReceiver.getInt("dailyWaterConsumption", 0);
        int bottleCapacity = userProfilePrefsReceiver.getInt("bottleCapacity", 0);
        int bottlesDone = mainPrefsReceiver.getInt("bottlesDone", 0);
        int bottlesDoneExtended = mainPrefsReceiver.getInt("bottlesDoneExtended", 0);
        int howMuchToDrink = dailyWaterConsumption - (bottlesDone * bottleCapacity);
        int waterToday = (bottlesDone + bottlesDoneExtended) * bottleCapacity;

        mainPrefsEditor.putInt("waterToday", waterToday).apply();
        mainPrefsEditor.putInt("howMuchToDrink", howMuchToDrink).apply();
    }
}