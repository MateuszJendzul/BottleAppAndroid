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

    TextView welcomeText, profileSetupText, showProfileText, mainTime, mainDate;
    Button profileEditButton, showProfileButton, showProfileButtonToast;
    static boolean enableShowProfileButton = false;

    @SuppressLint("SetTextI18n")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        // Log.d tags a message to method, so it will pop up in a log screen every time we call this method
        //  with custom text "Starting"
        Log.d(TAG, "onCreate: Starting");

        DateAndTime dateAndTime = new DateAndTime();

        // make new button object using previously setup id
        profileEditButton =  findViewById(R.id.profileEditButton);
        showProfileButton =  findViewById(R.id.showProfileButton);
        showProfileButtonToast = findViewById(R.id.showProfileButtonToast);

        // make new text object using previously setup id
        welcomeText = findViewById(R.id.welcomeText);
        profileSetupText = findViewById(R.id.profileSetupText);
        showProfileText = findViewById(R.id.showProfileText);
        mainTime = findViewById(R.id.time);
        mainDate = findViewById(R.id.date);

        welcomeText.setText("Welcome to your Bottle Application!");
        profileSetupText.setText("Edit profile:");
        showProfileText.setText("Show profile:");
        mainTime.setText(dateAndTime.getTime());
        mainDate.setText(dateAndTime.getDate());

        // puts transparent button on top of inactive profile button
        //  which is supposed to only show toast message and deactivate it
        //  when profile button becomes active
        showProfileButton.setEnabled(false);
        if (enableShowProfileButton) {
            showProfileButtonToast.setEnabled(false);
            showProfileButton.setEnabled(true);
        }

        // make event when button does something when clicked
        profileEditButton.setOnClickListener(new View.OnClickListener() {
            @Override
            // on button click
            public void onClick(View view) {
                Log.d(TAG, "onClick: profileEditButton");
                openProfileSetupScreen();
            }
        });

        showProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (enableShowProfileButton) {
                    Log.d(TAG, "onClick: showProfileButton(active)");
                    Intent intent = new Intent(MainActivity.this, ProfileScreen.class);
                    startActivity(intent);
                }
            }
        });

        showProfileButtonToast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: showProfileButton(inactive - toast)");
                showToast("Update your profile");
            }
        });

    }

    public void openProfileSetupScreen(){
        // change to new layout
        //  to make it work go to AndroidManifest and read comments
        Intent intent = new Intent(MainActivity.this, ProfileSetupScreen.class);
        startActivity(intent);
    }

    public void showToast(String text) {
        Toast.makeText(MainActivity.this, text, Toast.LENGTH_SHORT).show();
    }

}

//TODO find a solution to pass data between activities