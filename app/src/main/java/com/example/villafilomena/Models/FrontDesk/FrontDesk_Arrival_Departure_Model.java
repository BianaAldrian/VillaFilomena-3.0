package com.example.villafilomena.Models.FrontDesk;

public class FrontDesk_Arrival_Departure_Model {
    int count;
    String id, guestEmail, adultQty, kidQty, roomId, cottageId, total, balance, date;

    public FrontDesk_Arrival_Departure_Model(int count, String id, String guestEmail, String adultQty, String kidQty, String roomId, String cottageId, String total, String balance, String date) {
        this.count = count;
        this.id = id;
        this.guestEmail = guestEmail;
        this.adultQty = adultQty;
        this.kidQty = kidQty;
        this.roomId = roomId;
        this.cottageId = cottageId;
        this.total = total;
        this.balance = balance;
        this.date = date;
    }

    public int getCount() {
        return count;
    }

    public String getId() {
        return id;
    }

    public String getGuestEmail() {
        return guestEmail;
    }

    public String getAdultQty() {
        return adultQty;
    }

    public String getKidQty() {
        return kidQty;
    }

    public String getRoomId() {
        return roomId;
    }

    public String getCottageId() {
        return cottageId;
    }

    public String getTotal() {
        return total;
    }

    public String getBalance() {
        return balance;
    }

    public String getDate() {
        return date;
    }

}
