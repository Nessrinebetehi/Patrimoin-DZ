package com.example.patrimoin_dz;

public class User {
    private String uid;
    private String username;
    private String displayName;
    private String profilePictureUrl;
    private int postsCount;
    private int followersCount;
    private int followingCount;

    // Default constructor for Firestore
    public User() {}

    public User(String username, String displayName, String profilePictureUrl) {
        this.username = username;
        this.displayName = displayName;
        this.profilePictureUrl = profilePictureUrl;
        this.postsCount = 0;
        this.followersCount = 0;
        this.followingCount = 0;
    }

    public User(String username, String displayName, String profilePictureUrl, String uid) {
        this.username = username;
        this.displayName = displayName;
        this.profilePictureUrl = profilePictureUrl;
        this.uid = uid;
        this.postsCount = 0;
        this.followersCount = 0;
        this.followingCount = 0;
    }

    // Getters and setters
    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getProfilePictureUrl() {
        return profilePictureUrl;
    }

    public void setProfilePictureUrl(String profilePictureUrl) {
        this.profilePictureUrl = profilePictureUrl;
    }

    public int getPostsCount() {
        return postsCount;
    }

    public void setPostsCount(int postsCount) {
        this.postsCount = postsCount;
    }

    public int getFollowersCount() {
        return followersCount;
    }

    public void setFollowersCount(int followersCount) {
        this.followersCount = followersCount;
    }

    public int getFollowingCount() {
        return followingCount;
    }

    public void setFollowingCount(int followingCount) {
        this.followingCount = followingCount;
    }

    public CharSequence getProfileImageUrl() {
        return null;
    }
}