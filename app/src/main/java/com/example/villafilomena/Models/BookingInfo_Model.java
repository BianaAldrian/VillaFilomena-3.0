package com.example.villafilomena.Models;

public class BookingInfo_Model {
    String id, guest_email, checkIn_date, checkIn_time, checkOut_date, checkOut_time, adult_qty, kid_qty, room_id, cottage_id, total_payment, payment_status, GCash_number, reference_num, proofPay_url, receipt_url, bookings_status;

    public BookingInfo_Model(String id, String guest_email, String checkIn_date, String checkIn_time, String checkOut_date, String checkOut_time, String adult_qty, String kid_qty, String room_id, String cottage_id, String total_payment, String payment_status, String GCash_number, String reference_num, String proofPay_url, String receipt_url, String bookings_status) {
        this.id = id;
        this.guest_email = guest_email;
        this.checkIn_date = checkIn_date;
        this.checkIn_time = checkIn_time;
        this.checkOut_date = checkOut_date;
        this.checkOut_time = checkOut_time;
        this.adult_qty = adult_qty;
        this.kid_qty = kid_qty;
        this.room_id = room_id;
        this.cottage_id = cottage_id;
        this.total_payment = total_payment;
        this.payment_status = payment_status;
        this.GCash_number = GCash_number;
        this.reference_num = reference_num;
        this.proofPay_url = proofPay_url;
        this.receipt_url = receipt_url;
        this.bookings_status = bookings_status;
    }

    public String getId() {
        return id;
    }

    public String getGuest_email() {
        return guest_email;
    }

    public String getCheckIn_date() {
        return checkIn_date;
    }

    public String getCheckIn_time() {
        return checkIn_time;
    }

    public String getCheckOut_date() {
        return checkOut_date;
    }

    public String getCheckOut_time() {
        return checkOut_time;
    }

    public String getAdult_qty() {
        return adult_qty;
    }

    public String getKid_qty() {
        return kid_qty;
    }

    public String getRoom_id() {
        return room_id;
    }

    public String getCottage_id() {
        return cottage_id;
    }

    public String getTotal_payment() {
        return total_payment;
    }

    public String getPayment_status() {
        return payment_status;
    }

    public String getGCash_number() {
        return GCash_number;
    }

    public String getReference_num() {
        return reference_num;
    }

    public String getReceipt_url() {
        return receipt_url;
    }

    public String getBookings_status() {
        return bookings_status;
    }

    public String getProofPay_url() {
        return proofPay_url;
    }
}
