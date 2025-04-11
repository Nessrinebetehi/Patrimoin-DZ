package com.example.patrimoin_dz;

import java.io.Serializable;

public class User implements Serializable {
    private String username;
    private String displayName;
    private String profilePictureUrl;
    private int postsCount;
    private int followersCount;
    private int followingCount;

    public User(String username, String displayName, String profilePictureUrl, int postsCount, int followersCount, int followingCount) {
        this.username = username;
        this.displayName = displayName;
        this.profilePictureUrl = profilePictureUrl;
        this.postsCount = postsCount;
        this.followersCount = followersCount;
        this.followingCount = followingCount;
    }

    // Getters
    public String getUsername() {
        return username;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getProfilePictureUrl() {
        return profilePictureUrl;
    }

    public int getPostsCount() {
        return postsCount;
    }

    public int getFollowersCount() {
        return followersCount;
    }

    public int getFollowingCount() {
        return followingCount;
    }

    public void setPostsCount(int i) {
    }

    public void setFollowingCount(int i) {
    }

    public void setFollowersCount(int i) {
    }

    public void setProfilePictureUrl(String string) {

    }
}