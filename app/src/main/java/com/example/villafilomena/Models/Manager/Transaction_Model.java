package com.example.villafilomena.Models.Manager;

public class Transaction_Model {

    String id, email, date, total, refNum, receiptUrl;

    public Transaction_Model(String id, String email, String date, String total, String refNum, String receiptUrl) {
        this.id = id;
        this.email = email;
        this.date = date;
        this.total = total;
        this.refNum = refNum;
        this.receiptUrl = receiptUrl;
    }

    public String getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getDate() {
        return date;
    }

    public String getTotal() {
        return total;
    }

    public String getRefNum() {
        return refNum;
    }

    public String getReceiptUrl() {
        return receiptUrl;
    }
}
