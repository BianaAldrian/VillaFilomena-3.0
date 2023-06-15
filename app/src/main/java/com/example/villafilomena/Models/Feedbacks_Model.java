package com.example.villafilomena.Models;

public class Feedbacks_Model {
    String id, guest_email, ratings, feedback, image_urls, date;

    public Feedbacks_Model(String id, String guest_email, String ratings, String feedback, String image_urls, String date) {
        this.id = id;
        this.guest_email = guest_email;
        this.ratings = ratings;
        this.feedback = feedback;
        this.image_urls = image_urls;
        this.date = date;
    }

    public String getId() {
        return id;
    }

    public String getGuest_email() {
        return guest_email;
    }

    public String getRatings() {
        return ratings;
    }

    public String getFeedback() {
        return feedback;
    }

    public String getImage_urls() {
        return image_urls;
    }

    public String getDate() {
        return date;
    }

}
