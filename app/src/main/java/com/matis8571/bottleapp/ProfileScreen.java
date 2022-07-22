package com.matis8571.bottleapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class ProfileScreen extends AppCompatActivity {
    private final String TAG = "ProfileScreen";

    TextView weightText, bottleCapacityText, filterEfficiencyText, profileNameText, profileTime,
            profileDate;
    Button showToMainButton;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_screen);
        Log.d(TAG, "onCreate: Starting");

        DateAndTime dateAndTime = new DateAndTime();
        ProfileSetupScreen profileSetupScreen = new ProfileSetupScreen();

        showToMainButton =  findViewById(R.id.showToMainButton);
        weightText =  findViewById(R.id.weightText);
        bottleCapacityText =  findViewById(R.id.bottleCapacityText);
        filterEfficiencyText =  findViewById(R.id.filterEfficiencyText);
        profileNameText =  findViewById(R.id.setupProfileName);
        profileTime =  findViewById(R.id.profileTime);
        profileDate =  findViewById(R.id.profileDate);
//        TextView daysToChange = (TextView) findViewById(R.id.daysToChange);

//        daysToChange.setText("Filter change in: " + filter.getDaysCounter() + " days");
        profileTime.setText(dateAndTime.getTime());
        profileDate.setText(dateAndTime.getDate());

        weightText.setText("Your weight: " + profileSetupScreen.weightString);
        bottleCapacityText.setText("Bottle capacity: " + profileSetupScreen.bottleCapacityString);
        filterEfficiencyText.setText("Filter efficiency: " + profileSetupScreen.filterEfficiencyString);
        profileNameText.setText("Your name: " + profileSetupScreen.profileNameString);

        showToMainButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: Clicked: setupToMainButton");
                Intent intent = new Intent(ProfileScreen.this, com.matis8571.bottleapp.MainActivity.class);
                startActivity(intent);
            }
        });
    }

//    private String weightTextView() {
//        return "Your weight: " + profileSetupScreen.getWeight();
//    }
}


