package com.matis8571.bottleapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    //creates a tag for class
    private static final String TAG = "MainActivity";

    TextView welcomeText, profileSetupText, showProfileText, mainTimeText, mainDateText, alarmNotificationsText;
    Button profileEditButton, showProfileButton, showProfileButtonToast;
    protected static boolean enableShowProfileButton = false;
    DateAndTime dateAndTime = new DateAndTime();

    @SuppressLint("SetTextI18n")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        //Log.d tags a message to method, so it will pop up in a log screen every time we call this method
        // with custom text "Starting"
        Log.d(TAG, "onCreate: Starting");

        //make new button/text object using previously setup id
        profileEditButton = (Button) findViewById(R.id.profileEditButton);
        showProfileButton = (Button) findViewById(R.id.showProfileButton);
        showProfileButtonToast = (Button) findViewById(R.id.showProfileButtonToast);
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
                Intent profileEditButtonIntent = new Intent(MainActivity.this, ProfileSetupScreen.class);
                startActivity(profileEditButtonIntent);
            }
        });

        showProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (enableShowProfileButton) {
                    Log.d(TAG, "onClick: showProfileButton(active)");
                    Intent showProfileButtonIntent = new Intent(MainActivity.this, ProfileScreen.class);
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
    }
}