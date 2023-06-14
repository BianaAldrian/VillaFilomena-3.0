package com.example.villafilomena.Models.Manager;

import android.net.Uri;

public class Manager_addedImageModel {
    private Uri imageUri;

    public Manager_addedImageModel(Uri imageUri) {
        this.imageUri = imageUri;
    }

    public Uri getImageUri() {
        return imageUri;
    }

    public void setImageUri(Uri imageUri) {
        this.imageUri = imageUri;
    }
}
