package com.matis8571.bottleapp;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;

public class DateAndTime extends AppCompatActivity {
    private int day, month, year, timeHours, timeMinutes;
    Calendar calendar = Calendar.getInstance();

    public DateAndTime() {
        day = calendar.get(Calendar.DAY_OF_MONTH);
        month = calendar.get(Calendar.MONTH) + 1;
        year = calendar.get(Calendar.YEAR);
        timeHours = calendar.get(Calendar.HOUR_OF_DAY);
        timeMinutes = calendar.get(Calendar.MINUTE);
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

    /**
     * Returns actual time of the day using java.util.Calendar.
     */
    public String getTime() {
        return timeHours + ":" + timeMinutes;
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

    public String toString() {
        return timeHours + ":" + timeMinutes + ", " + day + "." + month + "." + year;
    }
}
