package com.matis8571.bottleapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    TextView welcomeText, profileSetupText, showProfileText, daysToChangeFilterMainText,
            showInMainDailyWaterConsumptionText, showInMainWaterDrunkText, helpText;
    Button profileEditButton, showProfileButton, addWaterButton, removeWaterButton, showProfileButtonToast,
            removeWaterButtonToast, addWaterButtonToast, waterConsumptionTipButton;
    EditText waterUserInputEdit;
    Calendar currentDateCalendar = Calendar.getInstance();

    @SuppressLint("SetTextI18n")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: Starting");
        setContentView(R.layout.main_activity_screen);
        dailyPropertiesReset();
        howMuchToDrink();
        countToFilterEfficiency();
        countDaysToFilterChange();
        userChangeAfterDaysFilterReset();
        reminderNotificationChannel1();
        reminderNotificationChannel2();
        reminderNotificationChannel3();

        SharedPreferences filterPrefsReceiver = getApplicationContext().getSharedPreferences(
                "filterPrefs", Context.MODE_PRIVATE);
        SharedPreferences mainPrefsReceiver = getApplicationContext().getSharedPreferences(
                "mainPrefs", Context.MODE_PRIVATE);

        boolean enableShowProfileButton = filterPrefsReceiver.getBoolean("enableShowProfileButton", false);
        boolean unlockNotifications = filterPrefsReceiver.getBoolean("unlockNotifications", false);
        int daysToFilterChangeCounting = mainPrefsReceiver.getInt("daysToFilterChangeCounting", 0);
        int dailyWaterConsumptionOnlyRead = filterPrefsReceiver.getInt("dailyWaterConsumptionOnlyRead", 0);
        int howMuchToDrinkMain = mainPrefsReceiver.getInt("howMuchToDrink", 0);
        int waterTodayMain = mainPrefsReceiver.getInt("waterToday", 0);

        //Make new button/text object using previously setup id
        profileEditButton = findViewById(R.id.profileEditButton);
        showProfileButton = findViewById(R.id.showProfileButton);
        addWaterButton = findViewById(R.id.addBottle);
        removeWaterButton = findViewById(R.id.removeBottle);
        waterConsumptionTipButton = findViewById(R.id.waterConsumptionTip);
        showProfileButtonToast = findViewById(R.id.showProfileButtonToast);
        removeWaterButtonToast = findViewById(R.id.removeBottleToast);
        addWaterButtonToast = findViewById(R.id.addBottleToast);
        welcomeText = findViewById(R.id.welcomeText);
        profileSetupText = findViewById(R.id.profileSetupText);
        showProfileText = findViewById(R.id.showProfileText);
        daysToChangeFilterMainText = findViewById(R.id.daysToChangeFilter);
        showInMainDailyWaterConsumptionText = findViewById(R.id.showInMainDailyWaterConsumption);
        showInMainWaterDrunkText = findViewById(R.id.showInMainWaterDrunk);
        helpText = findViewById(R.id.helpText);
        waterUserInputEdit = findViewById(R.id.waterUserInput);

        if (howMuchToDrinkMain < 1) {
            showInMainDailyWaterConsumptionText.setText("Water to drink: " + 0 + "ml");
        } else {
            showInMainDailyWaterConsumptionText.setText("Water to drink: " + howMuchToDrinkMain + "ml");
        }

        showInMainWaterDrunkText.setText("Today: " + waterTodayMain + "ml");
        daysToChangeFilterMainText.setText("Days left to filter change: " + daysToFilterChangeCounting);
        welcomeText.setText("Welcome to your Bottle Application!");
        profileSetupText.setText("Edit profile:");
        showProfileText.setText("Show profile:");
        helpText.setText("Tap for help:");

        //Put transparent buttons on top of original inactive buttons which are supposed to only show
        // toast message and deactivate when received boolean variable becomes true
        showProfileButton.setEnabled(false);
        addWaterButton.setEnabled(false);
        removeWaterButton.setEnabled(false);
        if (enableShowProfileButton) {
            addWaterButtonToast.setEnabled(false);
            removeWaterButtonToast.setEnabled(false);
            showProfileButtonToast.setEnabled(false);
            addWaterButton.setEnabled(true);
            removeWaterButton.setEnabled(true);
            showProfileButton.setEnabled(true);
        }

        profileEditButton.setOnClickListener(view -> {
            Log.d(TAG, "onClick: profileEditButton");
            Intent profileEditButtonIntent = new Intent(
                    MainActivity.this, ProfileSetupScreenActivity.class);
            startActivity(profileEditButtonIntent);
        });

        showProfileButton.setOnClickListener(view -> {
            if (enableShowProfileButton) {
                Log.d(TAG, "onClick: showProfileButton(active)");
                countToFilterEfficiency();
                Intent showProfileButtonIntent = new Intent(
                        MainActivity.this, ProfileScreenActivity.class);
                startActivity(showProfileButtonIntent);
            }
        });

        waterConsumptionTipButton.setOnClickListener(view -> {
            Log.d(TAG, "onClick: waterConsumptionTipButton");
            Intent waterConsumptionTipButtonIntent = new Intent(
                    MainActivity.this, WaterConsumptionTip.class);
            startActivity(waterConsumptionTipButtonIntent);
        });

        showProfileButtonToast.setOnClickListener(v -> {
            Log.d(TAG, "onClick: showProfileButton(inactive - toast)");
            //Shows little pop up on screen with custom text
            Toast.makeText(MainActivity.this, "Edit profile first", Toast.LENGTH_SHORT).show();
        });

        addWaterButtonToast.setOnClickListener(v -> {
            Log.d(TAG, "onClick: addBottleButton(inactive - toast");
            Toast.makeText(MainActivity.this, "Edit profile first", Toast.LENGTH_SHORT).show();
        });

        removeWaterButtonToast.setOnClickListener(v -> {
            Log.d(TAG, "onClick: removeBottleButton(inactive - toast");
            Toast.makeText(MainActivity.this, "Edit profile first", Toast.LENGTH_SHORT).show();
        });

        /*
          Use to add plus one bottle to bottlesDone or bottlesDoneExtended (depends on condition from
          addBottlesDone().
          After adding bottle calls howMuchToDrink() method to do the math and updates Text messages on
          application display.
         */
        addWaterButton.setOnClickListener(v -> {
            Log.d(TAG, "onClick: addWaterButton");
            addBottlesDone();
            howMuchToDrink();
            int waterToday = mainPrefsReceiver.getInt("waterToday", 0);
            int howMuchToDrink = mainPrefsReceiver.getInt("howMuchToDrink", 0);
            if (howMuchToDrink < 1) {
                showInMainDailyWaterConsumptionText.setText("Water to drink: " + 0 + "ml");
            } else {
                showInMainDailyWaterConsumptionText.setText("Water to drink: " + howMuchToDrink + "ml");
            }
            showInMainWaterDrunkText.setText("Today: " + waterToday + "ml");
        });

        /*
         Works almost the same as addBottleButton, but instead of adding removes one bottle and then
         updates Texts.
         */
        removeWaterButton.setOnClickListener(view -> {
            Log.d(TAG, "onClick: removeWaterButton");
            removeBottlesDone();
            howMuchToDrink();
            int waterToday = mainPrefsReceiver.getInt("waterToday", 0);
            int howMuchToDrink = mainPrefsReceiver.getInt("howMuchToDrink", 0);
            if (howMuchToDrink < 1) {
                showInMainDailyWaterConsumptionText.setText("Water to drink: " + 0 + "ml");
            } else {
                showInMainDailyWaterConsumptionText.setText("Water to drink: " + howMuchToDrink + "ml");
            }
            showInMainWaterDrunkText.setText("Today: " + waterToday + "ml");
        });
    }

    /**
     * Use to add notifications on channel1 created in MyService.class. Add only once a day
     * (to prevent adding again same notifications every time user re opens app).
     * Send notification for the last 3 days of filter efficiency which was set by user.
     */
    private void reminderNotificationChannel1() {
        Log.d(TAG, "onCall: reminderNotificationChannel1");
        MyService myService = new MyService(this);
        SharedPreferences mainPrefsReceiver = getApplicationContext().getSharedPreferences(
                "mainPrefs", Context.MODE_PRIVATE);
        SharedPreferences mainPrefs = getSharedPreferences("mainPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor mainPrefsEditor = mainPrefs.edit();
        int xChannel1 = mainPrefsReceiver.getInt("xChannel1", 3);
        int daysToFilterChangeCounting = mainPrefsReceiver.getInt("daysToFilterChangeCounting", 0);

        // Send notifications for the last 3 days of filter usage user set date
        if (xChannel1 == daysToFilterChangeCounting) {
            Calendar alarmAt19Ch1 = Calendar.getInstance();
            alarmAt19Ch1.setTimeInMillis(System.currentTimeMillis());
            alarmAt19Ch1.set(Calendar.HOUR_OF_DAY, 19);
            alarmAt19Ch1.set(Calendar.MINUTE, 0);
            alarmAt19Ch1.set(Calendar.SECOND, 1);
            myService.setReminder(alarmAt19Ch1, currentDateCalendar);

            xChannel1--;
            mainPrefsEditor.putInt("xChannel1", xChannel1).apply();
        }
    }

    /**
     * Use to add notifications on channel2 created in MyService.class. Add only once a day
     * (to prevent adding again same notifications every time user re opens app).
     * Every 2 hours (10-18) check if user consumed settled amount of water, if not, set to notify.
     */
    private void reminderNotificationChannel2() {
        Log.d(TAG, "onCall: reminderNotificationChannel2");
        MyService myService = new MyService(this);
        SharedPreferences mainPrefsReceiver = getApplicationContext().getSharedPreferences(
                "mainPrefs", Context.MODE_PRIVATE);

        int savedDay = mainPrefsReceiver.getInt("savedDay", 0);
        if (savedDay != getDay()) {
            SharedPreferences mainPrefs = getSharedPreferences("mainPrefs", Context.MODE_PRIVATE);
            SharedPreferences.Editor mainPrefsEditor = mainPrefs.edit();
            mainPrefsEditor.putInt("savedDay", getDay()).apply();
            mainPrefsEditor.putBoolean("addedToday", true).apply();
        }
        int howMuchToDrink = mainPrefsReceiver.getInt("howMuchToDrink", 0);
        boolean addedToday = mainPrefsReceiver.getBoolean("addedToday", false);

        if (howMuchToDrink > 0 && addedToday) {
            //Setup new calendar time to later build notification using it
            Calendar alarmAt10Ch2 = Calendar.getInstance();
            alarmAt10Ch2.setTimeInMillis(System.currentTimeMillis());
            alarmAt10Ch2.set(Calendar.HOUR_OF_DAY, 10);
            alarmAt10Ch2.set(Calendar.MINUTE, 0);
            alarmAt10Ch2.set(Calendar.SECOND, 1);
            //Call method to create notification using specified calendar time
            myService.setReminder(alarmAt10Ch2, currentDateCalendar);

            Calendar alarmAt12Ch2 = Calendar.getInstance();
            alarmAt12Ch2.setTimeInMillis(System.currentTimeMillis());
            alarmAt12Ch2.set(Calendar.HOUR_OF_DAY, 12);
            alarmAt12Ch2.set(Calendar.MINUTE, 0);
            alarmAt12Ch2.set(Calendar.SECOND, 1);
            myService.setReminder(alarmAt12Ch2, currentDateCalendar);

            Calendar alarmAt14Ch2 = Calendar.getInstance();
            alarmAt14Ch2.setTimeInMillis(System.currentTimeMillis());
            alarmAt14Ch2.set(Calendar.HOUR_OF_DAY, 14);
            alarmAt14Ch2.set(Calendar.MINUTE, 0);
            alarmAt14Ch2.set(Calendar.SECOND, 1);
            myService.setReminder(alarmAt14Ch2, currentDateCalendar);

            Calendar alarmAt16Ch2 = Calendar.getInstance();
            alarmAt16Ch2.setTimeInMillis(System.currentTimeMillis());
            alarmAt16Ch2.set(Calendar.HOUR_OF_DAY, 16);
            alarmAt16Ch2.set(Calendar.MINUTE, 0);
            alarmAt16Ch2.set(Calendar.SECOND, 1);
            myService.setReminder(alarmAt16Ch2, currentDateCalendar);

            Calendar alarmAt18Ch2 = Calendar.getInstance();
            alarmAt18Ch2.setTimeInMillis(System.currentTimeMillis());
            alarmAt18Ch2.set(Calendar.HOUR_OF_DAY, 18);
            alarmAt18Ch2.set(Calendar.MINUTE, 0);
            alarmAt18Ch2.set(Calendar.SECOND, 1);
            myService.setReminder(alarmAt18Ch2, currentDateCalendar);
        }
    }

    /**
     * Use to add notifications on channel3 created in MyService.class. Add only once a day
     * (to prevent adding again same notifications every time user re opens app).
     * Check if filter still can filter some water based on it efficiency, which was set by user.
     * Send notifications if filter can filter only 10l or less.
     */
    private void reminderNotificationChannel3() {
        Log.d(TAG, "onCall: reminderNotificationChannel3");
        MyService myService = new MyService(this);
        SharedPreferences mainPrefsReceiver = getApplicationContext().getSharedPreferences(
                "mainPrefs", Context.MODE_PRIVATE);
        SharedPreferences userProfilePrefsReceiver = getApplicationContext().getSharedPreferences(
                "userProfilePrefs", Context.MODE_PRIVATE);
        SharedPreferences mainPrefs = getSharedPreferences("mainPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor mainPrefsEditor = mainPrefs.edit();
        int filterEfficiencyCounting = mainPrefsReceiver.getInt("filterEfficiencyCounting", 0);
        int filterEfficiency = userProfilePrefsReceiver.getInt("filterEfficiency", 0);
        int howMuchToFilterLeft = filterEfficiency - (filterEfficiencyCounting / 1000);

        if (howMuchToFilterLeft <= 10) {
            Calendar alarmAt18Ch2 = Calendar.getInstance();
            alarmAt18Ch2.setTimeInMillis(System.currentTimeMillis());
            alarmAt18Ch2.set(Calendar.HOUR_OF_DAY, 19);
            alarmAt18Ch2.set(Calendar.MINUTE, 0);
            alarmAt18Ch2.set(Calendar.SECOND, 1);
            myService.setReminder(alarmAt18Ch2, currentDateCalendar);
        }
    }

    /**
     * Method used to count how much water is left until filter efficiency hits it's limit.
     */
    private void countToFilterEfficiency() {
        Log.d(TAG, "onCall: countToFilterEfficiency");
        SharedPreferences userProfilePrefsReceiver = getApplicationContext().getSharedPreferences(
                "userProfilePrefs", Context.MODE_PRIVATE);
        SharedPreferences mainPrefsReceiver = getApplicationContext().getSharedPreferences(
                "mainPrefs", Context.MODE_PRIVATE);
        int filterEfficiency = userProfilePrefsReceiver.getInt("filterEfficiency", 0);
        int bottleCapacity = userProfilePrefsReceiver.getInt("bottleCapacity", 0);
        int bottlesDoneToEfficiency = mainPrefsReceiver.getInt("bottlesDoneToEfficiency", 0);
        int bottlesDoneExtendedToEfficiency = mainPrefsReceiver.getInt("bottlesDoneExtendedToEfficiency", 0);
        int countingWaterUserInputToEfficiency = mainPrefsReceiver.getInt("countingWaterUserInputToEfficiency", 0);
        int filterEfficiencyCounting = ((bottlesDoneToEfficiency + bottlesDoneExtendedToEfficiency) *
                bottleCapacity) + countingWaterUserInputToEfficiency;
        SharedPreferences mainPrefs = getSharedPreferences("mainPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor mainPrefsEditor = mainPrefs.edit();
        mainPrefsEditor.putInt("filterEfficiencyCounting", filterEfficiencyCounting).apply();
    }

    /**
     * Call to reset every given variable once a day, or initiate any method. Checks if current
     * day equals X, if it's false executes its contents and then sets X as current day to reset
     * it for today.
     */
    private void dailyPropertiesReset() {
        Log.d(TAG, "onCall: dailyPropertiesReset");
        SharedPreferences mainPrefsReceiver = getApplicationContext().getSharedPreferences(
                "mainPrefs", Context.MODE_PRIVATE);
        SharedPreferences filterPrefsReceiver = getApplicationContext().getSharedPreferences(
                "filterPrefs", Context.MODE_PRIVATE);
        SharedPreferences myServicePrefsReceiver = getApplicationContext().getSharedPreferences(
                "myServicePrefs", Context.MODE_PRIVATE);
        SharedPreferences userProfilePrefsReceiver = getApplicationContext().getSharedPreferences(
                "userProfilePrefs", Context.MODE_PRIVATE);
        SharedPreferences mainPrefs = getSharedPreferences("mainPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor mainPrefsEditor = mainPrefs.edit();
        int x = mainPrefsReceiver.getInt("x", 0);
        int daysCounter = mainPrefsReceiver.getInt("daysCounter", 0);
        int userChangeAfterDays = filterPrefsReceiver.getInt("userChangeAfterDays", 0);
        int filterEfficiency = userProfilePrefsReceiver.getInt("filterEfficiency", 0);

        //Reset to default variables responsible for, counting days (adding one to result everyday)
        // and sending notifications about it
        if (daysCounter == userChangeAfterDays) {
            mainPrefsReceiver.edit().remove("daysCounter").apply();
            myServicePrefsReceiver.edit().remove("xChannel1").apply();
        }

        //Reset to default (provided by user when editing profile) properties responsible for
        // counting daily consumed water
        if (x != getDay()) {
            addToDaysCounter();
            mainPrefsReceiver.edit().remove("bottlesDone").apply();
            mainPrefsReceiver.edit().remove("bottlesDoneExtend").apply();
            mainPrefsReceiver.edit().remove("waterToday").apply();
            mainPrefsReceiver.edit().remove("countingWaterUserInput").apply();
            int howMuchToDrink = filterPrefsReceiver.getInt("dailyWaterConsumptionOnlyRead", 0);
            x = getDay();
            mainPrefsEditor.putInt("x", x).apply();
            mainPrefsEditor.putInt("howMuchToDrink", howMuchToDrink).apply();
        }

        //Reset properties responsible for counting filter efficiency after reaching target amount
        // and sending notifications about it
        int howMuchToFilterLeft = mainPrefsReceiver.getInt("howMuchToFilterLeft", 0);
        if (howMuchToFilterLeft == filterEfficiency) {
            mainPrefsReceiver.edit().remove("countingWaterUserInputToEfficiency").apply();
            mainPrefsReceiver.edit().remove("bottlesDoneExtendedToEfficiency").apply();
            mainPrefsReceiver.edit().remove("bottlesDoneToEfficiency").apply();
            myServicePrefsReceiver.edit().remove("xChannel3").apply();
        }
    }

    /**
     * Call to check if daysCounter reached user provided number as value when filter change is needed.
     * If statement if true, reset daysCounter.
     */
    private void userChangeAfterDaysFilterReset() {
        Log.d(TAG, "onCall: userChangeAfterDaysFilterReset");
        SharedPreferences filterPrefsReceiver = getApplicationContext().getSharedPreferences(
                "filterPrefs", Context.MODE_PRIVATE);
        SharedPreferences mainPrefsReceiver = getApplicationContext().getSharedPreferences(
                "mainPrefs", Context.MODE_PRIVATE);
        int userChangeAfterDays = filterPrefsReceiver.getInt("userChangeAfterDays", 0);
        int daysCounter = mainPrefsReceiver.getInt("daysCounter", 0);

        if (userChangeAfterDays == daysCounter) {
            mainPrefsReceiver.edit().remove("daysCounter").apply();
        }
    }

    /**
     * Method used to count how many days have passed to reach settled by user amount of days to next filter change.
     */
    private void countDaysToFilterChange() {
        Log.d(TAG, "onCall: countDaysToFilterChange");
        SharedPreferences mainPrefsReceiver = getApplicationContext().getSharedPreferences(
                "mainPrefs", Context.MODE_PRIVATE);
        SharedPreferences filterPrefsReceiver = getApplicationContext().getSharedPreferences(
                "filterPrefs", Context.MODE_PRIVATE);
        int daysCounter = mainPrefsReceiver.getInt("daysCounter", 0);
        int userChangeAfterDays = filterPrefsReceiver.getInt("userChangeAfterDays", 0);
        int daysToFilterChangeCounting = userChangeAfterDays - daysCounter;

        SharedPreferences mainPrefs = getSharedPreferences("mainPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor mainPrefsEditor = mainPrefs.edit();
        mainPrefsEditor.putInt("daysToFilterChangeCounting", daysToFilterChangeCounting).apply();
    }

    /**
     * Add one to bottlesDone if howMuchToDrink is equal or greater than bottleCapacity.
     * If howMuchToDrink totals less than bottleCapacity set it to 0 and add one to bottlesDone,
     * when howMuchToDrink equals 0 add one to bottlesDoneExtended.
     * Uses SharedPreferences to store and load data in order to save it from activity shutdown wipe.
     */

    private void addBottlesDone() {
        Log.d(TAG, "onCall: addBottlesDone");
        SharedPreferences mainPrefsReceiver = getApplicationContext().getSharedPreferences(
                "mainPrefs", Context.MODE_PRIVATE);
        SharedPreferences userProfilePrefsReceiver = getApplicationContext().getSharedPreferences(
                "userProfilePrefs", Context.MODE_PRIVATE);
        SharedPreferences mainPrefs = getSharedPreferences("mainPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor mainPrefsEditor = mainPrefs.edit();
        int bottlesDone = mainPrefsReceiver.getInt("bottlesDone", 0);
        int bottlesDoneExtended = mainPrefsReceiver.getInt("bottlesDoneExtended", 0);
        int bottleCapacity = userProfilePrefsReceiver.getInt("bottleCapacity", 0);
        int howMuchToDrink = mainPrefsReceiver.getInt("howMuchToDrink", 0);
        if (howMuchToDrink > 0) {
            if (waterUserInputEdit.getText().toString().isEmpty()) {
                bottlesDone++;
            } else {
                int countingWaterUserInput = mainPrefsReceiver.getInt("countingWaterUserInput", 0);
                countingWaterUserInput = countingWaterUserInput + Integer.parseInt(waterUserInputEdit.getText().toString());
                mainPrefsEditor.putInt("countingWaterUserInput", countingWaterUserInput).apply();
                mainPrefsEditor.putInt("countingWaterUserInputToEfficiency", countingWaterUserInput).apply();
            }
        } else if (howMuchToDrink <= 0) {
            if (waterUserInputEdit.getText().toString().isEmpty()) {
                bottlesDoneExtended++;
            } else {
                int countingWaterUserInput = mainPrefsReceiver.getInt("countingWaterUserInput", 0);
                countingWaterUserInput = countingWaterUserInput + Integer.parseInt(waterUserInputEdit.getText().toString());
                mainPrefsEditor.putInt("countingWaterUserInput", countingWaterUserInput).apply();
                mainPrefsEditor.putInt("countingWaterUserInputToEfficiency", countingWaterUserInput).apply();
            }
        } else {
            Toast.makeText(MainActivity.this, "Wrong water amount", Toast.LENGTH_SHORT).show();
        }

        mainPrefsEditor.putInt("bottlesDone", bottlesDone).apply();
        mainPrefsEditor.putInt("bottlesDoneToEfficiency", bottlesDone).apply();
        mainPrefsEditor.putInt("bottlesDoneExtended", bottlesDoneExtended).apply();
        mainPrefsEditor.putInt("bottlesDoneExtendedToEfficiency", bottlesDoneExtended).apply();
    }

    /**
     * Remove one from bottlesDone if howMuchToDrink is equal or greater than bottleCapacity.
     * If howMuchToDrink totals less than bottleCapacity set it to equal as bottleCapacity and remove
     * one from bottlesDone, when none of these conditions is true, then remove one from bottlesDoneExtended.
     * Uses SharedPreferences to store and load data in order to save it from activity shutdown wipe.
     */
    private void removeBottlesDone() {
        Log.d(TAG, "onCall: removeBottlesDone");
        SharedPreferences mainPrefsReceiver = getApplicationContext().getSharedPreferences(
                "mainPrefs", Context.MODE_PRIVATE);
        SharedPreferences mainPrefs = getSharedPreferences("mainPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor mainPrefsEditor = mainPrefs.edit();
        int bottlesDone = mainPrefsReceiver.getInt("bottlesDone", 0);
        int bottlesDoneExtended = mainPrefsReceiver.getInt("bottlesDoneExtended", 0);
        int howMuchToDrink = mainPrefsReceiver.getInt("howMuchToDrink", 0);

        if (howMuchToDrink > 0) {
            if (waterUserInputEdit.getText().toString().isEmpty()) {
                bottlesDone--;
            } else {
                int countingWaterUserInput = mainPrefsReceiver.getInt("countingWaterUserInput", 0);
                countingWaterUserInput = countingWaterUserInput - Integer.parseInt(waterUserInputEdit.getText().toString());
                mainPrefsEditor.putInt("countingWaterUserInput", countingWaterUserInput).apply();
                mainPrefsEditor.putInt("countingWaterUserInputToEfficiency", countingWaterUserInput).apply();

            }
        } else if (howMuchToDrink <= 0) {
            if (waterUserInputEdit.getText().toString().isEmpty()) {
                bottlesDoneExtended--;
            } else {
                int countingWaterUserInput = mainPrefsReceiver.getInt("countingWaterUserInput", 0);
                countingWaterUserInput = countingWaterUserInput - Integer.parseInt(waterUserInputEdit.getText().toString());
                mainPrefsEditor.putInt("countingWaterUserInput", countingWaterUserInput).apply();
                mainPrefsEditor.putInt("countingWaterUserInputToEfficiency", countingWaterUserInput).apply();
            }
        } else {
            Toast.makeText(MainActivity.this, "Wrong water amount", Toast.LENGTH_SHORT).show();
        }

        mainPrefsEditor.putInt("bottlesDone", bottlesDone).apply();
        mainPrefsEditor.putInt("bottlesDoneToEfficiency", bottlesDone).apply();
        mainPrefsEditor.putInt("bottlesDoneExtended", bottlesDoneExtended).apply();
        mainPrefsEditor.putInt("bottlesDoneExtendedToEfficiency", bottlesDoneExtended).apply();
    }

    /**
     * Counts how much water has left to reach daily (settled by user) consumption goal, as well as how much
     * water user has already consumed today and sends it as SharedPreferences to later display it on screen.
     */
    @SuppressLint({"SetTextI18n", "ApplySharedPref"})
    private void howMuchToDrink() {
        Log.d(TAG, "onCall: howMuchToDrink");
        SharedPreferences mainPrefsReceiver = getApplicationContext().getSharedPreferences(
                "mainPrefs", Context.MODE_PRIVATE);
        SharedPreferences filterPrefsReceiver = getApplicationContext().getSharedPreferences(
                "filterPrefs", Context.MODE_PRIVATE);
        SharedPreferences userProfilePrefsReceiver = getApplicationContext().getSharedPreferences(
                "userProfilePrefs", Context.MODE_PRIVATE);
        SharedPreferences mainPrefs = getSharedPreferences("mainPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor mainPrefsEditor = mainPrefs.edit();
        int countingWaterUserInput = mainPrefsReceiver.getInt("countingWaterUserInput", 0);
        int dailyWaterConsumption = filterPrefsReceiver.getInt("dailyWaterConsumption", 0);
        int bottleCapacity = userProfilePrefsReceiver.getInt("bottleCapacity", 0);
        int bottlesDone = mainPrefsReceiver.getInt("bottlesDone", 0);
        int bottlesDoneExtended = mainPrefsReceiver.getInt("bottlesDoneExtended", 0);
        int howMuchToDrink = dailyWaterConsumption - ((bottlesDone + bottlesDoneExtended) * bottleCapacity) - countingWaterUserInput;
        int waterToday = ((bottlesDone + bottlesDoneExtended) * bottleCapacity) + countingWaterUserInput;

        mainPrefsEditor.putInt("waterToday", waterToday).apply();
        mainPrefsEditor.putInt("howMuchToDrink", howMuchToDrink).apply();
    }

    /**
     * Call to add one to daysCounter.
     */
    private void addToDaysCounter() {
        Log.d(TAG, "onCall: addToDaysCounter");
        SharedPreferences mainPrefsReceiver = getApplicationContext().getSharedPreferences(
                "mainPrefs", Context.MODE_PRIVATE);
        int daysCounter = mainPrefsReceiver.getInt("daysCounter", 0);
        daysCounter++;
        SharedPreferences mainPrefs = getSharedPreferences("mainPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor mainPrefsEditor = mainPrefs.edit();
        mainPrefsEditor.putInt("daysCounter", daysCounter).apply();
    }

    private int getTimeHours() {
        return currentDateCalendar.get(Calendar.HOUR_OF_DAY);
    }

    private int getTimeMinutes() {
        return currentDateCalendar.get(Calendar.MINUTE);
    }

    private int getTimeSeconds() {
        return currentDateCalendar.get(Calendar.SECOND);
    }

    private int getDay() {
        return currentDateCalendar.get(Calendar.DAY_OF_MONTH);
    }

    private int getMonth() {
        return currentDateCalendar.get(Calendar.MONTH) + 1;
    }

    private int getYear() {
        return currentDateCalendar.get(Calendar.YEAR);
    }

}