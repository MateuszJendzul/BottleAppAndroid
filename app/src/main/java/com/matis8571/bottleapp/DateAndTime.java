package com.matis8571.bottleapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Calendar;

public class DateAndTime extends AppCompatActivity {
    @SuppressWarnings("FieldMayBeFinal")
    private int day, month, year, timeHour, timeMinute, timeSeconds;
    Calendar calendar = Calendar.getInstance();

    public DateAndTime() {
        day = calendar.get(Calendar.DAY_OF_MONTH);
        month = calendar.get(Calendar.MONTH) + 1;
        year = calendar.get(Calendar.YEAR);
        timeHour = calendar.get(Calendar.HOUR_OF_DAY);
        timeMinute = calendar.get(Calendar.MINUTE);
        timeSeconds = calendar.get(Calendar.SECOND);
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

    public int getTimeMinute() {
        return timeMinute;
    }

    public int getTimeHour() {
        return timeHour;
    }

    public int getTimeSeconds(){
        return timeSeconds;
    }

    /**
     * Return actual time of the day using java.util.Calendar.
     */
    public String getTime() {
        if (timeMinute < 10) {
            return timeHour + ":0" + timeMinute;
        } else {
            return timeHour + ":" + timeMinute;
        }
    }

    /**
     * Return actual date using java.util.Calendar.
     * If number representing month is one digital, adds 0 to make it two-digit.
     */
    public String getDate() {
        if (getMonth() < 9) {
            return getDay() + ".0" + getMonth() + "." + getYear();
        } else {
            return getDay() + "." + getMonth() + "." + getYear();
        }
    }

    /**
     * Change to new variable at 23:59.
     * @param toReset target value to reset
     * @param resetTo set new value after reset
     * @return returns new value
     */
    public int dailyReset(int toReset, int resetTo) {
        if (timeHour == 23 && timeMinute == 59) {
            toReset = resetTo;
        }
        return resetTo;
    }

    /**
     * At 23:59 return target boolean as false, otherwise return true.
     * @param toReset target boolean to change
     * @return returns false at 23:59
     */
    public boolean dailyReset(boolean toReset) {
        if (timeHour == 23 && timeMinute == 59) {
            return !toReset;
        } else {
            return toReset;
        }
    }

    @NonNull
    public String toString() {
        return "Time and date: " + timeHour + ":" + timeMinute + ", " + day + "." + month + "." + year;
    }
}
