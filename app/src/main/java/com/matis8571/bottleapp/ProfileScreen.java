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

public class ProfileScreen extends AppCompatActivity {
    private final String TAG = "ProfileScreen";

    TextView weightText, bottleCapacityText, filterEfficiencyText, profileNameText, profileTimeText,
            profileDateText, setupMessageText, daysToChangeText, filterStartDateText;
    Button showToMainButton;
    private int daysCounter, x, savedDay, savedMonth, savedYear;
    DateAndTime dateAndTime = new DateAndTime();

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_screen);
        Log.d(TAG, "onCreate: Starting");
        check();

        showToMainButton = (Button) findViewById(R.id.showToMainButton);
        weightText = (TextView) findViewById(R.id.profileScreenWeight);
        bottleCapacityText = (TextView) findViewById(R.id.profileScreenBottleCapacity);
        filterEfficiencyText = (TextView) findViewById(R.id.profileScreenFilterEfficiency);
        profileNameText = (TextView) findViewById(R.id.profileScreenName);
        profileTimeText = (TextView) findViewById(R.id.profileTime);
        profileDateText = (TextView) findViewById(R.id.profileDate);
        setupMessageText = (TextView) findViewById(R.id.setupMessage);
        daysToChangeText = (TextView) findViewById(R.id.daysToChange);
        filterStartDateText = (TextView) findViewById(R.id.filterStartDate);

        //to get values from userProfilePrefsEditor create new SharedPreferences object userProfilePrefsReceiver
        // use name made in the activity which variable is from "userProfilePrefs" (ProfileSetupScreen.class)
        SharedPreferences userProfilePrefsReceiver = getApplicationContext().getSharedPreferences(
                "userProfilePrefs", Context.MODE_PRIVATE);
        SharedPreferences filterPrefsReceiver = getApplicationContext().getSharedPreferences(
                "filterPrefs", Context.MODE_PRIVATE);
        //to receive values from other activity make new corresponding variable and assign it to
        // a name of variable which was previously send to Editor "weightString" you can then add
        // default value set to variables defaults
        String weightString = userProfilePrefsReceiver.getString("weightString", null);
        String bottleCapacityString = userProfilePrefsReceiver.getString("bottleCapacityString", null);
        String filterEfficiencyString = userProfilePrefsReceiver.getString("filterEfficiencyString", null);
        String profileNameString = userProfilePrefsReceiver.getString("profileNameString", null);
        String filterDay = filterPrefsReceiver.getString("filterDay", null);
        String filterMonth = filterPrefsReceiver.getString("filterMonth", null);
        savedYear = filterPrefsReceiver.getInt("savedYear", 0);
        savedMonth = Integer.parseInt(filterMonth);
        savedDay = Integer.parseInt(filterDay);

        weightText.setText("Weight: " + weightString + " kg");
        bottleCapacityText.setText("Bottle capacity: " + bottleCapacityString + " ml");
        filterEfficiencyText.setText("Filter efficiency: " + filterEfficiencyString + " l");
        profileNameText.setText("Name: " + profileNameString);
        filterStartDateText.setText("Filter start: " + filterStartDateAutoString());
        setupMessageText.setText("Profile:");
        profileTimeText.setText(dateAndTime.getTime());
        profileDateText.setText(dateAndTime.getDate());
        if (daysCounter > 31) {
            // TODO add Main notification
            daysToChangeText.setText("Change filter!\nLast change: " + daysCounter + " days ago.");
        } else if (daysCounter == 1) {
            daysToChangeText.setText("Filter usage: " + daysCounter + " day");
        } else {
            daysToChangeText.setText("Filter usage: " + daysCounter + " days");
        }


        showToMainButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: Clicked: setupToMainButton");
                Intent showToMainButtonIntent = new Intent(ProfileScreen.this, MainActivity.class);
                startActivity(showToMainButtonIntent);
            }
        });
    }

    //method called to initiate every Check Method at once
    public void check() {
        countingDaysToMaxEfficiency();
//        countingWaterToMaxEfficiency();
    }

    public int getSavedDay() {
        return savedDay;
    }

    public int getSavedMonth() {
        return savedMonth;
    }

    public int getSavedYear() {
        return savedYear;
    }

    //CM call to check if current day equals X, if false adds 1 to daysCounter and sets to true to reset for today
    private void countingDaysToMaxEfficiency() {
        if (x != dateAndTime.getDay()) {
            daysCounter = daysCounter + 1;
            x = dateAndTime.getDay();
        }
    }

    private String filterStartDateAutoString() {
        return savedDay + "." + savedMonth + "." + savedYear;
    }
}
