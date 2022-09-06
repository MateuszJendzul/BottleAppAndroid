package com.matis8571.bottleapp;
//TODO add reset method, so after settled amount of days variables will reset to original values

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    TextView welcomeText, profileSetupText, showProfileText, daysToChangeFilterText,
            showInMainDailyWaterConsumptionText, showInMainWaterDrunkText;
    Button profileEditButton, showProfileButton, addBottleButton, removeBottleButton, showProfileButtonToast,
            removeBottleButtonToast, addBottleButtonToast;
    private int howMuchToDrink, waterToday;
    DateAndTime dateAndTime = new DateAndTime();

    @SuppressLint("SetTextI18n")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity_screen);
        Log.d(TAG, "onCreate: Starting");
        dailyPropertiesReset();
        howMuchToDrink();
        countToFilterEfficiency();
        startMyService();

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
        showInMainWaterDrunkText.setText("Today: " + waterToday + "ml");
        daysToChangeFilterText.setText("Days left to filter change: " + (userChangeAfterDays - daysCounter));
        welcomeText.setText("Welcome to your Bottle Application!");
        profileSetupText.setText("Edit profile:");
        showProfileText.setText("Show profile:");

        //puts transparent buttons on top of original inactive buttons which are supposed to only show
        // toast message and deactivate when received boolean variable becomes true
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

        /*
          Button used to check if user already consumed target amount of water by adding plus one bottle to
          equation which is used to count how much water user have already consumed. If target is reached
          or is going to be on negative (because user exceeded daily target by drinking more than
          previously settled amount) sets displayed value as 0.
          After adding bottle calls howMuchToDrink() method to do the math and updates Text messages on
          application display.
         */
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

        /*
         Works almost the same as addBottleButton, but instead of adding removes one bottle and then updates Texts.
         */
        removeBottleButton.setOnClickListener(view -> {
            if (howMuchToDrink >= bottleCapacity) {
                removeBottlesDone();
            } else //noinspection ConstantConditions
                if (howMuchToDrink < bottleCapacity) {
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
     * Method called to reset every given variables once a day. Checks if current day equals X,
     * if it's false executes its contents and then sets X as current day to reset it for today.
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

    /**
     * Method used to count how much water is left until filter efficiency hits it's limit.
     */
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
        daysCounter++;
        int countDaysToFilterChange = daysCounter - userChangeAfterDays;
        SharedPreferences mainPrefs = getSharedPreferences("mainPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor mainPrefsEditor = mainPrefs.edit();
        mainPrefsEditor.putInt("daysCounter", daysCounter).apply();
        mainPrefsEditor.putInt("countDaysToFilterChange", countDaysToFilterChange).apply();
    }

    /**
     * Adds one to amount of bottles consumed by user. Uses SharedPreferences to store
     * and load data in order to save it from activity shutdown wipe.
     */
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

    /**
     * Removes one from amount of bottles consumed by user. Uses SharedPreferences to store
     * and load data in order to save it from activity shutdown wipe.
     */
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

    /**
     * Adds one to extended amount of bottles consumed by user (if user decides to exceed
     * settled daily consumption limit).
     */
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

    /**
     * Removes one from extended amount of bottles consumed by user (if user decides to exceed
     * settled daily consumption limit).
     */
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
        int dailyWaterConsumption = filterPrefsReceiver.getInt("dailyWaterConsumption", 0);
        int bottleCapacity = userProfilePrefsReceiver.getInt("bottleCapacity", 0);
        int bottlesDone = mainPrefsReceiver.getInt("bottlesDone", 0);
        int bottlesDoneExtended = mainPrefsReceiver.getInt("bottlesDoneExtended", 0);
        int howMuchToDrink = dailyWaterConsumption - (bottlesDone * bottleCapacity);
        int waterToday = (bottlesDone + bottlesDoneExtended) * bottleCapacity;

        mainPrefsEditor.putInt("waterToday", waterToday).apply();
        mainPrefsEditor.putInt("howMuchToDrink", howMuchToDrink).apply();
    }

    private void startMyService() {
        Intent myServiceIntent = new Intent(MainActivity.this, MyService.class);
        startService(myServiceIntent);
    }
}