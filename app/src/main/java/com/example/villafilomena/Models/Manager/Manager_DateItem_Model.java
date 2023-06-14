package com.example.villafilomena.Models.Manager;

public class Manager_DateItem_Model {
    private int day;
    private int month;
    private int year;

    public Manager_DateItem_Model(int day, int month, int year) {
        this.day = day;
        this.month = month;
        this.year = year;
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
}
