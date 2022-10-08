package com.matis8571.bottleapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class ProfileScreenActivity extends AppCompatActivity {
    private final String TAG = "ProfileScreen";

    TextView weightText, bottleCapacityText, filterEfficiencyText, profileNameText,
            profileMessageText, daysCounterText, filterStartDateText, changeAfterDaysText,
            dailyWaterConsumptionText, waterTodayText, countDaysToFilterChangeText, daysToFilterChangeText;
    Button showToMainButton;
    private int savedDay, savedMonth, savedYear;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_screen);
        Log.d(TAG, "onCreate: Starting");

        showToMainButton = findViewById(R.id.showToMainButton);
        dailyWaterConsumptionText = findViewById(R.id.dailyWaterConsumption);
        changeAfterDaysText = findViewById(R.id.changeAfterDays);
        weightText = findViewById(R.id.profileScreenWeight);
        bottleCapacityText = findViewById(R.id.profileScreenBottleCapacity);
        filterEfficiencyText = findViewById(R.id.profileScreenFilterEfficiency);
        profileNameText = findViewById(R.id.profileScreenName);
        profileMessageText = findViewById(R.id.profileMessage);
        daysCounterText = findViewById(R.id.daysCounter);
        filterStartDateText = findViewById(R.id.filterStartDate);
        waterTodayText = findViewById(R.id.waterToday);
        countDaysToFilterChangeText = findViewById(R.id.countDaysToFilterChange);
        daysToFilterChangeText = findViewById(R.id.daysToFilterChange);

        SharedPreferences userProfilePrefsReceiver = getApplicationContext().getSharedPreferences(
                "userProfilePrefs", Context.MODE_PRIVATE);
        SharedPreferences filterPrefsReceiver = getApplicationContext().getSharedPreferences(
                "filterPrefs", Context.MODE_PRIVATE);
        SharedPreferences mainPrefsReceiver = getApplicationContext().getSharedPreferences(
                "mainPrefs", Context.MODE_PRIVATE);

        String profileName = userProfilePrefsReceiver.getString("profileName", null);
        int userWeight = userProfilePrefsReceiver.getInt("userWeight", 0);
        int bottleCapacity = userProfilePrefsReceiver.getInt("bottleCapacity", 0);
        int filterEfficiency = userProfilePrefsReceiver.getInt("filterEfficiency", 0);
        String filterDay = filterPrefsReceiver.getString("filterDay", null);
        String filterMonth = filterPrefsReceiver.getString("filterMonth", null);
        int userChangeAfterDays = filterPrefsReceiver.getInt("userChangeAfterDays", 0);
        int dailyWaterConsumptionOnlyRead = filterPrefsReceiver.getInt("dailyWaterConsumption", 0);
        int daysCounter = mainPrefsReceiver.getInt("daysCounter", 0);
        int waterToday = mainPrefsReceiver.getInt("waterToday", 0);
        int daysToFilterChangeCounting = mainPrefsReceiver.getInt("daysToFilterChangeCounting", 0);
        int filterEfficiencyCounting = mainPrefsReceiver.getInt("filterEfficiencyCounting", 0);

        double filterEfficiencyCountingProjection = (filterEfficiency - (double) filterEfficiencyCounting / 1000);

        savedYear = filterPrefsReceiver.getInt("savedYear", 0);
        savedMonth = Integer.parseInt(filterMonth);
        savedDay = Integer.parseInt(filterDay);

        SharedPreferences profilePrefs = getSharedPreferences("profilePrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor profilePrefsEditor = profilePrefs.edit();
        profilePrefsEditor.putInt("daysCounter", daysCounter);
        profilePrefsEditor.apply();

        dailyWaterConsumptionText.setText("Daily water consumption: " + dailyWaterConsumptionOnlyRead + "ml");
        weightText.setText("Weight: " + userWeight + "kg");
        bottleCapacityText.setText("Bottle capacity: " + bottleCapacity + "ml");
        filterEfficiencyText.setText("Filter efficiency: " + filterEfficiency + "l");
        profileNameText.setText("Name: " + profileName);
        filterStartDateText.setText("Filter start: " + filterStartDateString());
        profileMessageText.setText("Profile:");
        waterTodayText.setText("Water today: " + waterToday);
        countDaysToFilterChangeText.setText("To filter change: " + daysToFilterChangeCounting + " days");
        daysToFilterChangeText.setText("To filter change: " + filterEfficiencyCountingProjection + "l");

        if (daysCounter > userChangeAfterDays) {
            daysCounterText.setText("Change filter!\nLast change: " + daysCounter + " days ago.");
        } else if (daysCounter == 1) {
            daysCounterText.setText("Filter usage: " + daysCounter + " day");
        } else {
            daysCounterText.setText("Filter usage: " + daysCounter + " days");
        }

        if (userChangeAfterDays == 1) {
            changeAfterDaysText.setText("Filter change after: " + userChangeAfterDays + " day");
        } else {
            changeAfterDaysText.setText("Filter change after: " + userChangeAfterDays + " days");
        }

        showToMainButton.setOnClickListener(view -> {
            Log.d(TAG, "onClick: Clicked: setupToMainButton");
            Intent showToMainButtonIntent = new Intent(ProfileScreenActivity.this, MainActivity.class);
            startActivity(showToMainButtonIntent);
        });
    }

    /**
     * Displays date in dd/mm/yyyy format using previously saved user input in FilterSetup.java
     */
    private String filterStartDateString() {
        if (savedMonth < 10) {
            return savedDay + "." + "0" + savedMonth + "." + savedYear;
        } else {
            return savedDay + "." + savedMonth + "." + savedYear;
        }
    }
}
