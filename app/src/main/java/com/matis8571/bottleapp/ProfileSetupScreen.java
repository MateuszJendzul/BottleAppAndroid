package com.matis8571.bottleapp;

import android.annotation.SuppressLint;
import android.content.Intent;
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

    TextView setupMessage, profileSetupTime, profileSetupDate;
    Button setupToMainButton, submitButton;
    EditText nameEdit, weightEdit, filterEfficiencyEdit, bottleCapacityEdit;
    String weightString, bottleCapacityString, filterEfficiencyString, profileNameString;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_setup_layout);
        Log.d(TAG, "onCreate: Starting");

        DateAndTime dateAndTime = new DateAndTime();

        setupMessage = findViewById(R.id.setupMessage);
        profileSetupTime = findViewById(R.id.profileSetupTime);
        profileSetupDate = findViewById(R.id.profileSetupDate);
        setupToMainButton = findViewById(R.id.setupToMainButton);
        submitButton = findViewById(R.id.submitButton);
        nameEdit = findViewById(R.id.setupProfileName);
        weightEdit = findViewById(R.id.setupWeight);
        filterEfficiencyEdit = findViewById(R.id.setupFilterEfficiency);
        bottleCapacityEdit = findViewById(R.id.setupBottleCapacity);

        setupMessage.setText("Input the following:");
        profileSetupTime.setText(dateAndTime.getTime());
        profileSetupDate.setText(dateAndTime.getDate());


        setupToMainButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: Clicked: setupToMainButton");
                Intent intent = new Intent(ProfileSetupScreen.this, MainActivity.class);
                startActivity(intent);
            }
        });

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // enable showProfileButton if every variable from profileSetupScreen is set
                if (weightEdit.getText().toString().isEmpty() || nameEdit.getText().toString().isEmpty() ||
                        bottleCapacityEdit.getText().toString().isEmpty() ||
                        filterEfficiencyEdit.getText().toString().isEmpty()
                ) {
                    // toast message without showToast method
                    Toast.makeText(ProfileSetupScreen.this, "Empty fields", Toast.LENGTH_SHORT).show();

                } else {
                    // assigns user input to a String variable
                    weightString = weightEdit.getText().toString();
                    profileNameString = nameEdit.getText().toString();
                    bottleCapacityString = bottleCapacityEdit.getText().toString();
                    filterEfficiencyString = filterEfficiencyEdit.getText().toString();

                    // in order to make boolean work as intended use reference to class not class instance
                    MainActivity.enableShowProfileButton = true;
                }

                showToast("Profile saved");
                // use String.valueOf(bottleCapacity) to display integer if String is needed

            }
        });
    }

    // after interaction shows little pop up on screen with custom text
    public void showToast(String text) {
        Toast.makeText(ProfileSetupScreen.this, text, Toast.LENGTH_SHORT).show();
    }


}
