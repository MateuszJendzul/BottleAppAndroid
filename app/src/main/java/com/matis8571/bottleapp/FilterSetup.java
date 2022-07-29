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

public class FilterSetup extends AppCompatActivity {
    private final String TAG = "ProfileScreen";

    TextView filterMessageText;
    EditText filterStartDayEdit, filterStartMonthEdit;
    Button filterSetupBackButton, submitFilterButton, submitFilterButtonToast, filterSetupToMainButton;
    DateAndTime dateAndTime = new DateAndTime();

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.filter_layout);
        Log.d(TAG, "onCreate: Starting");

        submitFilterButtonToast = (Button) findViewById(R.id.submitFilterButtonToast);
        filterSetupToMainButton = (Button) findViewById(R.id.filterSetupToMainButton);
        filterSetupBackButton = (Button) findViewById(R.id.filterSetupBackButton);
        submitFilterButton = (Button) findViewById(R.id.submitFilterButton);
        filterMessageText = (TextView) findViewById(R.id.filterMessage);
        filterStartDayEdit = (EditText) findViewById(R.id.filterStartDay);
        filterStartMonthEdit = (EditText) findViewById(R.id.filterStartMonth);

        filterMessageText.setText("Filter usage start date:");

        submitFilterButton.setEnabled(false);
        submitFilterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: submitFilterButton");
                String filterDay = filterStartDayEdit.getText().toString();
                String filterMonth = filterStartMonthEdit.getText().toString();
                int savedYear = dateAndTime.getYear();

                SharedPreferences filterPrefs = getSharedPreferences("filterPrefs", Context.MODE_PRIVATE);
                SharedPreferences.Editor filterPrefsEditor = filterPrefs.edit();
                filterPrefsEditor.putString("filterDay", filterDay);
                filterPrefsEditor.putString("filterMonth", filterMonth);
                filterPrefsEditor.putInt("savedYear", savedYear);
                filterPrefsEditor.apply();

                Toast.makeText(FilterSetup.this, "Filter updated", Toast.LENGTH_SHORT).show();
            }
        });

        submitFilterButtonToast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: submitFilterButton(inactive - toast)");

                //checks if user input fields are empty then if filterStartDayEdit and filterStartMonthEdit
                // fields doesn't exceed set values disables submitFilterButtonToast button and enables
                // submitFilterButton and allows to upload input data, but if one exceeds, shows Toast
                if (filterStartDayEdit.getText().toString().isEmpty() && filterStartMonthEdit.getText().toString().isEmpty()) {
                    Toast.makeText(FilterSetup.this, "Empty fields", Toast.LENGTH_SHORT).show();
                } else if (Integer.parseInt(filterStartDayEdit.getText().toString()) <= 31 &&
                        Integer.parseInt(filterStartMonthEdit.getText().toString()) <= 12) {
                    submitFilterButtonToast.setEnabled(false);
                    submitFilterButton.setEnabled(true);
                    Toast.makeText(FilterSetup.this, "Update now", Toast.LENGTH_SHORT).show();
                } else if (Integer.parseInt(filterStartDayEdit.getText().toString()) > 31 ||
                        Integer.parseInt(filterStartMonthEdit.getText().toString()) > 12) {
                    Toast.makeText(FilterSetup.this, "Day or month exceeds its max value",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        filterSetupBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: filterToMainButton");
                Intent filterToMainButtonIntent = new Intent(
                        FilterSetup.this, ProfileSetupScreen.class);
                startActivity(filterToMainButtonIntent);
            }
        });

        filterSetupToMainButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: filterSetupToMainButton");
                Intent filterSetupToMainButtonIntent = new Intent(
                        FilterSetup.this, MainActivity.class);
                startActivity(filterSetupToMainButtonIntent);
            }
        });
    }
}
