package com.example.villafilomena.Models.Manager;

public class Manager_frondeskClerk_Model {
    String id, imageUrl, clerkName, clerkContact, clerkUsername, clerkPassword;

    public Manager_frondeskClerk_Model(String id, String imageUrl, String clerkName, String clerkContact, String clerkUsername, String clerkPassword) {
        this.id = id;
        this.imageUrl = imageUrl;
        this.clerkName = clerkName;
        this.clerkContact = clerkContact;
        this.clerkUsername = clerkUsername;
        this.clerkPassword = clerkPassword;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getClerkName() {
        return clerkName;
    }

    public void setClerkName(String clerkName) {
        this.clerkName = clerkName;
    }

    public String getClerkContact() {
        return clerkContact;
    }

    public void setClerkContact(String clerkContact) {
        this.clerkContact = clerkContact;
    }

    public String getClerkUsername() {
        return clerkUsername;
    }

    public void setClerkUsername(String clerkUsername) {
        this.clerkUsername = clerkUsername;
    }

    public String getClerkPassword() {
        return clerkPassword;
    }

    public void setClerkPassword(String clerkPassword) {
        this.clerkPassword = clerkPassword;
    }
}
