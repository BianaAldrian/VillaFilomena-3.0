package com.example.villafilomena.Models;

public class Video_Model {
    String id, video_url;

    public Video_Model(String id, String video_url) {
        this.id = id;
        this.video_url = video_url;
    }

    public String getId() {
        return id;
    }

    public String getVideo_url() {
        return video_url;
    }
}
