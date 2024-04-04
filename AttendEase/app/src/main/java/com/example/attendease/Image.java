package com.example.attendease;

/**
 * Represents an image object with its URL.
 */
public class Image {
    private String imageUrl;

    public Image() {
    }

    public Image(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
