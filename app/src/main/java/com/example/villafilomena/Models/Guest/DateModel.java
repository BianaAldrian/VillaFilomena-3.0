package com.example.villafilomena.Models.Guest;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class DateModel {
    private String monthYear;

    public DateModel(String monthYear) {
        this.monthYear = monthYear;
    }

    public String getMonthYear() {
        return monthYear;
    }

    // Extract the month from the monthYear string
    public int getMonth() {
        SimpleDateFormat format = new SimpleDateFormat("MMMM yyyy", Locale.getDefault());
        Calendar calendar = Calendar.getInstance();
        try {
            calendar.setTime(format.parse(monthYear));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return calendar.get(Calendar.MONTH);
    }

    // Extract the year from the monthYear string
    public int getYear() {
        SimpleDateFormat format = new SimpleDateFormat("MMMM yyyy", Locale.getDefault());
        Calendar calendar = Calendar.getInstance();
        try {
            calendar.setTime(format.parse(monthYear));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return calendar.get(Calendar.YEAR);
    }
}
