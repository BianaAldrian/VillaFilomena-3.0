package com.example.villafilomena.Models;

public class Schedule_Model {
    String id, disable_date, reason;

    public Schedule_Model(String id, String disable_date, String reason) {
        this.id = id;
        this.disable_date = disable_date;
        this.reason = reason;
    }

    public String getId() {
        return id;
    }

    public String getDisable_date() {
        return disable_date;
    }

    public String getReason() {
        return reason;
    }
}
