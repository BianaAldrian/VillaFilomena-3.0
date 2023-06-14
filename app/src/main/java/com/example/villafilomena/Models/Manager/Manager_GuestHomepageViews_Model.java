package com.example.villafilomena.Models.Manager;

public class Manager_GuestHomepageViews_Model {

    //banner
    private String bannerId, bannerUrl;

    //introduction
    private String introId, introduction;

    //videos
    private String videoId, videoUrl;

    //images
    private String imageId, imageUrl;

    public Manager_GuestHomepageViews_Model(String bannerId, String bannerUrl, String introId, String introduction, String videoId, String videoUrl, String imageId, String imageUrl) {
        this.bannerId = bannerId;
        this.bannerUrl = bannerUrl;
        this.introId = introId;
        this.introduction = introduction;
        this.videoId = videoId;
        this.videoUrl = videoUrl;
        this.imageId = imageId;
        this.imageUrl = imageUrl;
    }

    public String getBannerId() {
        return bannerId;
    }

    public void setBannerId(String bannerId) {
        this.bannerId = bannerId;
    }

    public String getBannerUrl() {
        return bannerUrl;
    }

    public void setBannerUrl(String bannerUrl) {
        this.bannerUrl = bannerUrl;
    }

    public String getIntroId() {
        return introId;
    }

    public void setIntroId(String introId) {
        this.introId = introId;
    }

    public String getIntroduction() {
        return introduction;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }

    public String getVideoId() {
        return videoId;
    }

    public void setVideoId(String videoId) {
        this.videoId = videoId;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public String getImageId() {
        return imageId;
    }

    public void setImageId(String imageId) {
        this.imageId = imageId;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
