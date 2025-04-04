package com.example.patrimoin_dz;

public class MusicCard {
    private String title;
    private int imageResId;
    private String description;

    public MusicCard(String title, int imageResId, String description) {
        this.title = title;
        this.imageResId = imageResId;
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public int getImageResId() {
        return imageResId;
    }

    public String getDescription() {
        return description;
    }
}