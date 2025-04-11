package com.example.patrimoin_dz;

public class ChatStory {
    private String userName;
    private int profileImage;

    public ChatStory(String userName, int profileImage) {
        this.userName = userName;
        this.profileImage = profileImage;
    }

    public String getUserName() {
        return userName;
    }

    public int getProfileImage() {
        return profileImage;
    }
}