package com.matis8571.bottleapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class ProfileSetupScreen extends AppCompatActivity {
    private static final String TAG = "ProfileSetupScreen";

    TextView setupMessageText, profileSetupTimeText, profileSetupDateText;
    Button setupToMainButton, submitButton, setupToFilterSetupButton;
    EditText nameEdit, weightEdit, filterEfficiencyEdit, bottleCapacityEdit;
    DateAndTime dateAndTime = new DateAndTime();

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_setup_layout);
        Log.d(TAG, "onCreate: Starting");

        setupToFilterSetupButton = (Button) findViewById(R.id.setupToFilterSetupButton);
        setupToMainButton = (Button) findViewById(R.id.setupToMainButton);
        submitButton = (Button) findViewById(R.id.submitChangesButton);
        setupMessageText = (TextView) findViewById(R.id.setupMessage);
        profileSetupTimeText = (TextView) findViewById(R.id.profileSetupTime);
        profileSetupDateText = (TextView) findViewById(R.id.profileSetupDate);
        nameEdit = (EditText) findViewById(R.id.setupProfileName);
        weightEdit = (EditText) findViewById(R.id.setupWeight);
        filterEfficiencyEdit = (EditText) findViewById(R.id.setupFilterEfficiency);
        bottleCapacityEdit = (EditText) findViewById(R.id.setupBottleCapacity);

        setupMessageText.setText("Input the following:");
        profileSetupTimeText.setText(dateAndTime.getTime());
        profileSetupDateText.setText(dateAndTime.getDate());

        //in order to send any variables between activities use SharedPreferences
        // create variable name userProfilePrefs, and then a name to identify it with "userProfilePrefs"
        // lastly set up a Context mode
        SharedPreferences userProfilePrefs = getSharedPreferences("userProfilePrefs", Context.MODE_PRIVATE);

        setupToMainButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: setupToMainButton");
                Intent setupToMainButtonIntent = new Intent(ProfileSetupScreen.this, MainActivity.class);
                startActivity(setupToMainButtonIntent);
            }
        });

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //enable showProfileButton if every variable from profileSetupScreen is set
                if (weightEdit.getText().toString().isEmpty() || nameEdit.getText().toString().isEmpty() ||
                        bottleCapacityEdit.getText().toString().isEmpty() ||
                        filterEfficiencyEdit.getText().toString().isEmpty()
                ) {
                    Log.d(TAG, "onClick: submitButton (empty fields)");
                    Toast.makeText(ProfileSetupScreen.this, "Empty fields", Toast.LENGTH_SHORT).show();

                } else {
                    Log.d(TAG, "onClick: submitButton");
                    //assign user input to a String array variables
                    String weightString = weightEdit.getText().toString();
                    String profileNameString = nameEdit.getText().toString();
                    String bottleCapacityString = bottleCapacityEdit.getText().toString();
                    String filterEfficiencyString = filterEfficiencyEdit.getText().toString();

                    //create SharedPreferences.Editor and assigns previously created SharedPreferences variable to it
                    SharedPreferences.Editor userProfilePrefsEditor = userProfilePrefs.edit();
                    //to send specific variables put them under custom name "weightString"
                    // and then call which one you want to send weightString
                    userProfilePrefsEditor.putString("weightString", weightString);
                    userProfilePrefsEditor.putString("profileNameString", profileNameString);
                    userProfilePrefsEditor.putString("bottleCapacityString", bottleCapacityString);
                    userProfilePrefsEditor.putString("filterEfficiencyString", filterEfficiencyString);
                    //after setting everything .apply(); to the Editor
                    userProfilePrefsEditor.apply();

                    //in order to make boolean work as intended use reference to class not class instance
                    MainActivity.enableShowProfileButton = true;
                }
                Toast.makeText(ProfileSetupScreen.this, "Profile updated", Toast.LENGTH_SHORT).show();
            }
        });

        setupToFilterSetupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: setupToFilterSetupButton");

                Intent setupToFilterSetupButtonIntent = new Intent(
                        ProfileSetupScreen.this, FilterSetup.class);
                startActivity(setupToFilterSetupButtonIntent);
            }
        });
    }
}