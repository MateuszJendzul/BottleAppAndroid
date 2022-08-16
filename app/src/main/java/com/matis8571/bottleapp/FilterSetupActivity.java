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

import org.w3c.dom.Text;

public class FilterSetupActivity extends AppCompatActivity {
    private static final String TAG = "ProfileScreen";

    TextView filterDateSetupText, userSetDaysToChangeText, userSetDailyWaterUsageText, filterDateText, filterTimeText;
    EditText filterStartDayEdit, filterStartMonthEdit, filterDaysToChangeEdit, dailyWaterConsumptionEdit;
    Button filterSetupBackButton, submitFilterButton, submitFilterButtonToast, filterSetupToMainButton;
    DateAndTime dateAndTime = new DateAndTime();

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.filter_setup_layout);
        Log.d(TAG, "onCreate: Starting");

        submitFilterButtonToast = (Button) findViewById(R.id.submitFilterButtonToast);
        filterSetupToMainButton = (Button) findViewById(R.id.filterSetupToMainButton);
        filterSetupBackButton = (Button) findViewById(R.id.filterSetupBackButton);
        submitFilterButton = (Button) findViewById(R.id.submitFilterButton);
        filterDateText = (TextView) findViewById(R.id.filterDate);
        filterTimeText = (TextView) findViewById(R.id.filterTime);
        userSetDailyWaterUsageText = (TextView) findViewById(R.id.userSetDailyWaterUsageMessage);
        userSetDaysToChangeText = (TextView) findViewById(R.id.userSetDaysToChangeMessage);
        filterDateSetupText = (TextView) findViewById(R.id.filterDateSetupMessage);
        dailyWaterConsumptionEdit = (EditText) findViewById(R.id.dailyWaterConsumptionInput);
        filterStartDayEdit = (EditText) findViewById(R.id.filterStartDayInput);
        filterStartMonthEdit = (EditText) findViewById(R.id.filterStartMonthInput);
        filterDaysToChangeEdit = (EditText) findViewById(R.id.filterDaysToChangeInput);

        filterDateText.setText(dateAndTime.getDate());
        filterTimeText.setText(dateAndTime.getTime());
        userSetDailyWaterUsageText.setText("Daily water consumption:");
        userSetDaysToChangeText.setText("Filter change after:");
        filterDateSetupText.setText("Filter start date:");

        submitFilterButton.setEnabled(false);
        submitFilterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: submitFilterButton");
                String filterDay = filterStartDayEdit.getText().toString();
                String filterMonth = filterStartMonthEdit.getText().toString();
                int dailyWaterConsumption = Integer.parseInt(dailyWaterConsumptionEdit.getText().toString());
                int userChangeAfterDays = Integer.parseInt(filterDaysToChangeEdit.getText().toString());
                int savedYear = dateAndTime.getYear();

                SharedPreferences filterPrefs = getSharedPreferences("filterPrefs", Context.MODE_PRIVATE);
                SharedPreferences.Editor filterPrefsEditor = filterPrefs.edit();
                filterPrefsEditor.putString("filterDay", filterDay);
                filterPrefsEditor.putString("filterMonth", filterMonth);
                filterPrefsEditor.putInt("dailyWaterConsumption", dailyWaterConsumption);
                filterPrefsEditor.putInt("dailyWaterConsumptionOnlyRead", dailyWaterConsumption);
                filterPrefsEditor.putInt("userChangeAfterDays", userChangeAfterDays);
                filterPrefsEditor.putInt("savedYear", savedYear);
                filterPrefsEditor.apply();

                Toast.makeText(FilterSetupActivity.this, "Filter updated", Toast.LENGTH_SHORT).show();

                Intent submitFilterButtonIntent = new Intent(FilterSetupActivity.this, MainActivity.class);
                startActivity(submitFilterButtonIntent);
            }
        });

        submitFilterButtonToast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: submitFilterButton(inactive - toast)");

                //checks if user input fields are empty then if filterStartDayEdit and filterStartMonthEdit
                // fields doesn't exceed set values disables submitFilterButtonToast button and enables
                // submitFilterButton and allows to upload input data, but if one exceeds, shows Toast
                if (filterStartDayEdit.getText().toString().isEmpty() || filterStartMonthEdit.getText().toString().isEmpty()
                        || filterDaysToChangeEdit.getText().toString().isEmpty()) {
                    Toast.makeText(FilterSetupActivity.this, "Empty fields", Toast.LENGTH_SHORT).show();
                } else if (Integer.parseInt(filterStartDayEdit.getText().toString()) > 31 ||
                        Integer.parseInt(filterStartMonthEdit.getText().toString()) > 12) {
                    Toast.makeText(FilterSetupActivity.this, "Day or month exceeds its max value",
                            Toast.LENGTH_SHORT).show();
                } else if (Integer.parseInt(filterStartDayEdit.getText().toString()) <= 31 &&
                        Integer.parseInt(filterStartMonthEdit.getText().toString()) <= 12) {
                    submitFilterButtonToast.setEnabled(false);
                    submitFilterButton.setEnabled(true);
                    Toast.makeText(FilterSetupActivity.this, "Submit now", Toast.LENGTH_SHORT).show();
                } else if (Integer.parseInt(dailyWaterConsumptionEdit.getText().toString()) < 0 || 
                        dailyWaterConsumptionEdit.getText().toString().isEmpty()){
                    Toast.makeText(FilterSetupActivity.this, "Incorrect daily water demand",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        filterSetupBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: filterToMainButton");
                Intent filterToMainButtonIntent = new Intent(
                        FilterSetupActivity.this, ProfileSetupScreenActivity.class);
                startActivity(filterToMainButtonIntent);
            }
        });

        filterSetupToMainButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: filterSetupToMainButton");
                Intent filterSetupToMainButtonIntent = new Intent(
                        FilterSetupActivity.this, MainActivity.class);
                startActivity(filterSetupToMainButtonIntent);
            }
        });
    }
}
