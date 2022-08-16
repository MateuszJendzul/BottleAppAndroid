package com.matis8571.bottleapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class ProfileScreenActivity extends AppCompatActivity {
    private final String TAG = "ProfileScreen";

    TextView weightText, bottleCapacityText, filterEfficiencyText, profileNameText, profileTimeText,
            profileDateText, profileMessageText, daysCounterText, filterStartDateText, changeAfterDaysText,
            dailyWaterConsumptionText;
    Button showToMainButton;
    private int savedDay, savedMonth, savedYear;
    DateAndTime dateAndTime = new DateAndTime();

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_screen);
        Log.d(TAG, "onCreate: Starting");

        showToMainButton = (Button) findViewById(R.id.showToMainButton);
        dailyWaterConsumptionText = (TextView) findViewById(R.id.dailyWaterConsumption);
        changeAfterDaysText = (TextView) findViewById(R.id.changeAfterDays);
        weightText = (TextView) findViewById(R.id.profileScreenWeight);
        bottleCapacityText = (TextView) findViewById(R.id.profileScreenBottleCapacity);
        filterEfficiencyText = (TextView) findViewById(R.id.profileScreenFilterEfficiency);
        profileNameText = (TextView) findViewById(R.id.profileScreenName);
        profileTimeText = (TextView) findViewById(R.id.profileTime);
        profileDateText = (TextView) findViewById(R.id.profileDate);
        profileMessageText = (TextView) findViewById(R.id.profileMessage);
        daysCounterText = (TextView) findViewById(R.id.daysCounter);
        filterStartDateText = (TextView) findViewById(R.id.filterStartDate);

        //to get values from userProfilePrefsEditor create new SharedPreferences object userProfilePrefsReceiver
        // use name made in the activity which variable is from "userProfilePrefs" (ProfileSetupScreen.class)
        SharedPreferences userProfilePrefsReceiver = getApplicationContext().getSharedPreferences(
                "userProfilePrefs", Context.MODE_PRIVATE);
        SharedPreferences filterPrefsReceiver = getApplicationContext().getSharedPreferences(
                "filterPrefs", Context.MODE_PRIVATE);
        SharedPreferences mainPrefsReceiver = getApplicationContext().getSharedPreferences(
                "mainPrefs", Context.MODE_PRIVATE);

        //to receive values from other activity make new corresponding variable and assign it to
        // a name of variable which was previously send to Editor "weightString" you can then add
        // default value set to variables defaults
        String profileName = userProfilePrefsReceiver.getString("profileName", null);
        int weight = userProfilePrefsReceiver.getInt("weight", 0);
        int bottleCapacity = userProfilePrefsReceiver.getInt("bottleCapacity", 0);
        int filterEfficiency = userProfilePrefsReceiver.getInt("filterEfficiency", 0);
        String filterDay = filterPrefsReceiver.getString("filterDay", null);
        String filterMonth = filterPrefsReceiver.getString("filterMonth", null);
        int userChangeAfterDays = filterPrefsReceiver.getInt("userChangeAfterDays", 0);
        int dailyWaterConsumptionOnlyRead = filterPrefsReceiver.getInt("dailyWaterConsumption", 0);
        int daysCounter = mainPrefsReceiver.getInt("daysCounter", 0);
        savedYear = filterPrefsReceiver.getInt("savedYear", 0);
        savedMonth = Integer.parseInt(filterMonth);
        savedDay = Integer.parseInt(filterDay);

        SharedPreferences profilePrefs = getSharedPreferences("profilePrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor profilePrefsEditor = profilePrefs.edit();
        profilePrefsEditor.putInt("daysCounter", daysCounter);
        profilePrefsEditor.apply();

        dailyWaterConsumptionText.setText("Daily water consumption: " + dailyWaterConsumptionOnlyRead + " ml");
        weightText.setText("Weight: " + weight + " kg");
        bottleCapacityText.setText("Bottle capacity: " + bottleCapacity + " ml");
        filterEfficiencyText.setText("Filter efficiency: " + filterEfficiency + " l");
        profileNameText.setText("Name: " + profileName);
        filterStartDateText.setText("Filter start: " + filterStartDateString());
        profileMessageText.setText("Profile:");
        profileTimeText.setText(dateAndTime.getTime());
        profileDateText.setText(dateAndTime.getDate());
        if (daysCounter > userChangeAfterDays) {
            daysCounterText.setText("Change filter!\nLast change: " + daysCounter + " days ago.");
        } else if (daysCounter == 1) {
            daysCounterText.setText("Filter usage: " + daysCounter + " day");
        } else {
            daysCounterText.setText("Filter usage: " + daysCounter + " days");
        }

        if (userChangeAfterDays == 1){
            changeAfterDaysText.setText("Filter change after: " + userChangeAfterDays + " day");
        }else{
            changeAfterDaysText.setText("Filter change after: " + userChangeAfterDays + " days");
        }


        showToMainButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: Clicked: setupToMainButton");
                Intent showToMainButtonIntent = new Intent(ProfileScreenActivity.this, MainActivity.class);
                startActivity(showToMainButtonIntent);
            }
        });
    }

    /**
     * Displays date in dd/mm/yyyy format using previously saved user input in FilterSetup.java
     */
    private String filterStartDateString() {
        return savedDay + "." + savedMonth + "." + savedYear;
    }
}
