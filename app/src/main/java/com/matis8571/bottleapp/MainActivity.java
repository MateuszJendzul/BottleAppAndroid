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

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    TextView welcomeText, profileSetupText, showProfileText, daysToChangeFilterMainText,
            showInMainDailyWaterConsumptionText, showInMainWaterDrunkText, helpText;
    Button profileEditButton, showProfileButton, addWaterButton, removeWaterButton, showProfileButtonToast,
            removeWaterButtonToast, addWaterButtonToast, waterConsumptionTipButton;
    EditText waterUserInputEdit;
    MyService myService = new MyService();

    @SuppressLint("SetTextI18n")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: Starting");
        setContentView(R.layout.main_activity_screen);
        dailyPropertiesReset();
        monthlyPropertiesReset();
        howMuchToDrink();
        countToFilterEfficiency();
        countDaysToFilterChange();
        userChangeAfterDaysFilterReset();

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

        if (unlockNotifications) {
            startMyService();
        }

        //make new button/text object using previously setup id
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

        //puts transparent buttons on top of original inactive buttons which are supposed to only show
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
            Intent waterConsumptionTipButtonIntent = new Intent(
                    MainActivity.this, WaterConsumptionTip.class);
            startActivity(waterConsumptionTipButtonIntent);
        });

        showProfileButtonToast.setOnClickListener(v -> {
            Log.d(TAG, "onClick: showProfileButton(inactive - toast)");
            //shows little pop up on screen with custom text
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
     * Method used to count how much water is left until filter efficiency hits it's limit.
     */
    private void countToFilterEfficiency() {
        SharedPreferences userProfilePrefsReceiver = getApplicationContext().getSharedPreferences(
                "userProfilePrefs", Context.MODE_PRIVATE);
        int bottleCapacity = userProfilePrefsReceiver.getInt("bottleCapacity", 0);
        SharedPreferences mainPrefsReceiver = getApplicationContext().getSharedPreferences(
                "mainPrefs", Context.MODE_PRIVATE);
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
        SharedPreferences mainPrefsReceiver = getApplicationContext().getSharedPreferences(
                "mainPrefs", Context.MODE_PRIVATE);
        SharedPreferences filterPrefsReceiver = getApplicationContext().getSharedPreferences(
                "filterPrefs", Context.MODE_PRIVATE);
        SharedPreferences mainPrefs = getSharedPreferences("mainPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor mainPrefsEditor = mainPrefs.edit();
        int x = mainPrefsReceiver.getInt("x", 0);

        if (x != myService.getDay()) {
            addToDaysCounter();
            mainPrefsReceiver.edit().remove("bottlesDone").apply();
            mainPrefsReceiver.edit().remove("bottlesDoneExtend").apply();
            mainPrefsReceiver.edit().remove("waterToday").apply();
            mainPrefsReceiver.edit().remove("countingWaterUserInput").apply();
            int howMuchToDrink = filterPrefsReceiver.getInt("dailyWaterConsumptionOnlyRead", 0);
            x = myService.getDay();
            mainPrefsEditor.putInt("x", x).apply();
            mainPrefsEditor.putInt("howMuchToDrink", howMuchToDrink).apply();
        }
    }

    /**
     * Similar to daily variable, call to reset every given variable once a month, or initiate any method.
     */
    private void monthlyPropertiesReset() {
        SharedPreferences mainPrefsReceiver = getApplicationContext().getSharedPreferences(
                "mainPrefs", Context.MODE_PRIVATE);
        SharedPreferences filterPrefsReceiver = getApplicationContext().getSharedPreferences(
                "filterPrefs", Context.MODE_PRIVATE);
        int x = mainPrefsReceiver.getInt("x", 0);
        int userChangeAfterDays = filterPrefsReceiver.getInt("userChangeAfterDays", 0);

        if (x == userChangeAfterDays) {
            mainPrefsReceiver.edit().remove("bottlesDoneExtendedToEfficiency").apply();
            mainPrefsReceiver.edit().remove("bottlesDoneToEfficiency").apply();
            mainPrefsReceiver.edit().remove("countingWaterUserInputToEfficiency").apply();
        }
    }

    /**
     * Call to check if daysCounter reached user provided number as value when filter change is needed.
     * If statement if true, reset daysCounter.
     */
    private void userChangeAfterDaysFilterReset() {
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
        SharedPreferences mainPrefsReceiver = getApplicationContext().getSharedPreferences(
                "mainPrefs", Context.MODE_PRIVATE);
        SharedPreferences filterPrefsReceiver = getApplicationContext().getSharedPreferences(
                "filterPrefs", Context.MODE_PRIVATE);
        int daysCounter = mainPrefsReceiver.getInt("daysCounter", 0);
        int userChangeAfterDays = filterPrefsReceiver.getInt("userChangeAfterDays", 0);
        int daysToFilterChangeCounting = 0;
        if(userChangeAfterDays > 0) {
             daysToFilterChangeCounting = userChangeAfterDays - daysCounter;
        }

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
                int countingWaterUserInput = mainPrefsReceiver.getInt("countingWaterUserInput",0);
                countingWaterUserInput = countingWaterUserInput + Integer.parseInt(waterUserInputEdit.getText().toString());
                mainPrefsEditor.putInt("countingWaterUserInput", countingWaterUserInput).apply();
                mainPrefsEditor.putInt("countingWaterUserInputToEfficiency", countingWaterUserInput).apply();
            }
        } else if (howMuchToDrink <= 0) {
            if (waterUserInputEdit.getText().toString().isEmpty()) {
                bottlesDoneExtended++;
            } else {
                int countingWaterUserInput = mainPrefsReceiver.getInt("countingWaterUserInput",0);
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
                int countingWaterUserInput = mainPrefsReceiver.getInt("countingWaterUserInput",0);
                countingWaterUserInput = countingWaterUserInput - Integer.parseInt(waterUserInputEdit.getText().toString());
                mainPrefsEditor.putInt("countingWaterUserInput", countingWaterUserInput).apply();
                mainPrefsEditor.putInt("countingWaterUserInputToEfficiency", countingWaterUserInput).apply();

            }
        } else if (howMuchToDrink <= 0) {
            if (waterUserInputEdit.getText().toString().isEmpty()) {
                bottlesDoneExtended--;
            } else {
                int countingWaterUserInput = mainPrefsReceiver.getInt("countingWaterUserInput",0);
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
        SharedPreferences mainPrefsReceiver = getApplicationContext().getSharedPreferences(
                "mainPrefs", Context.MODE_PRIVATE);
        int daysCounter = mainPrefsReceiver.getInt("daysCounter", 0);
        daysCounter++;
        SharedPreferences mainPrefs = getSharedPreferences("mainPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor mainPrefsEditor = mainPrefs.edit();
        mainPrefsEditor.putInt("daysCounter", daysCounter).apply();
    }

    private void startMyService() {
        startService(new Intent(this, MyService.class));
    }
}