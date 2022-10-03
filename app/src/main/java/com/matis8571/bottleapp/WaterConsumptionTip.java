package com.matis8571.bottleapp;

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class WaterConsumptionTip extends AppCompatActivity {
    private static final String TAG = "WaterConsumptionTip";
    TextView waterConsumptionTipText;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: Starting");
        setContentView(R.layout.water_consumption_tip_layout);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        int width = displayMetrics.widthPixels;
        int height = displayMetrics.heightPixels;

        getWindow().setLayout((int) (width * 0.8), (int) (height * 0.3));

        waterConsumptionTipText = findViewById(R.id.waterConsumptionTip);
        String text = "* Amount to: refers to amount of water which you want to either mark as consumed" +
                ", or as amount to add to today's water target." +
                "\n* To do so, type amount on which you want to operate and then tap ADD to add, " +
                "or REMOVE to remove that amount to/from today's target." +
                "\n* Or leave empty to add/remove default amount which is equal to bottle capacity.";
        waterConsumptionTipText.setText(text);
    }
}
