package com.example.villafilomena.Models;

public class Image_Model {
    String id, image_url;

    public Image_Model(String id, String image_url) {
        this.id = id;
        this.image_url = image_url;
    }

    public String getId() {
        return id;
    }

    public String getImage_url() {
        return image_url;
    }
}
