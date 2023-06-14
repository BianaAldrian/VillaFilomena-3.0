package com.example.villafilomena.Models.Manager;

public class Manager_GuestInfo_Model {
    private final String email;
    private final String fullName;
    private final String contact;

    public Manager_GuestInfo_Model(String email, String fullName, String contact) {
        this.email = email;
        this.fullName = fullName;
        this.contact = contact;
    }

    public String getEmail() {
        return email;
    }

    public String getFullName() {
        return fullName;
    }

    public String getContact() {
        return contact;
    }
}
