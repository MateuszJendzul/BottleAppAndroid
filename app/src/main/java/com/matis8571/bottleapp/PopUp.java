package com.matis8571.bottleapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class PopUp extends AppCompatActivity {

    TextView popUpDailyExtendText, showInMainDailyWaterConsumptionText;
    EditText popUpDailyExtendEdit;
    Button popUpSubmit;
    boolean popUpSubmitBoolean = false;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.popup);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        int width = displayMetrics.widthPixels;
        int height = displayMetrics.heightPixels;

        getWindow().setLayout((int) (width * 0.8), (int) (height * 0.6));

        popUpSubmit = (Button) findViewById(R.id.popUpSubmit);
        popUpDailyExtendText = (TextView) findViewById(R.id.popUpDailyExtendText);
        showInMainDailyWaterConsumptionText = (TextView) findViewById(R.id.showInMainDailyWaterConsumption);
        popUpDailyExtendEdit = (EditText) findViewById(R.id.popUpDailyExtendEdit);

        popUpDailyExtendText.setText("Daily water limit acquired\nDo you want to add more bottles to today's limit?");

        popUpSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (popUpDailyExtendEdit.getText().toString().isEmpty()) {
                    Toast.makeText(PopUp.this, "Type value before submitting", Toast.LENGTH_SHORT).show();
                } else {
                    int extendDailyWaterConsumption = Integer.parseInt(popUpDailyExtendEdit.getText().toString());

                    SharedPreferences userProfilePrefsReceiver = getApplicationContext().getSharedPreferences(
                            "userProfilePrefs", Context.MODE_PRIVATE);
                    int bottleCapacity = userProfilePrefsReceiver.getInt("bottleCapacity", 0);

                    int howMuchToDrink = extendDailyWaterConsumption * bottleCapacity;

                    SharedPreferences sharedPreferencesPopUp = getSharedPreferences("sharedPreferencesPopUp",
                            Context.MODE_PRIVATE);
                    SharedPreferences.Editor sharedPreferencesPopUpEditor = sharedPreferencesPopUp.edit();
                    sharedPreferencesPopUpEditor.putInt("howMuchToDrink", howMuchToDrink);
                    sharedPreferencesPopUpEditor.apply();

                    popUpSubmitBoolean = true;
                    Toast.makeText(PopUp.this, "Daily target extended", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
