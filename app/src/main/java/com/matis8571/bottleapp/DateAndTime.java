package com.matis8571.bottleapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;

public class DateAndTime extends AppCompatActivity {
    private int day, month, year, timeHour, timeMinute;
    Calendar calendar = Calendar.getInstance();

    public DateAndTime() {
        day = calendar.get(Calendar.DAY_OF_MONTH);
        month = calendar.get(Calendar.MONTH) + 1;
        year = calendar.get(Calendar.YEAR);
        timeHour = calendar.get(Calendar.HOUR_OF_DAY);
        timeMinute = calendar.get(Calendar.MINUTE);
    }

    public int getDay() {
        return day;
    }

    public int getMonth() {
        return month;
    }

    public int getYear() {
        return year;
    }

    public int getTimeHour(){
        return timeHour;
    }

    /**
     * Returns actual time of the day using java.util.Calendar.
     */
    public String getTime() {
        if(timeMinute < 10){
            return timeHour + ":0" + timeMinute;
        }else {
            return timeHour + ":" + timeMinute;
        }
    }

    /**
     * Returns actual date using java.util.Calendar.
     * If number representing month is one digital, adds 0 to make it two-digit.
     */
    public String getDate() {
        if (getMonth() < 9) {
            return getDay() + ".0" + getMonth() + "." + getYear();
        } else {
            return getDay() + "." + getMonth() + "." + getYear();
        }
    }

    @NonNull
    public String toString() {
        return "Time and date: " + timeHour + ":" + timeMinute + ", " + day + "." + month + "." + year;
    }
}
