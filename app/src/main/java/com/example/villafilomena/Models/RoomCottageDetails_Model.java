package com.example.villafilomena.Models;

public class RoomCottageDetails_Model {
    String id, imageUrl, name, capacity, rate, description;

    public RoomCottageDetails_Model(String id, String imageUrl, String name, String capacity, String rate, String description) {
        this.id = id;
        this.imageUrl = imageUrl;
        this.name = name;
        this.capacity = capacity;
        this.rate = rate;
        this.description = description;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCapacity() {
        return capacity;
    }

    public void setCapacity(String capacity) {
        this.capacity = capacity;
    }

    public String getRate() {
        return rate;
    }

    public void setRate(String rate) {
        this.rate = rate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
