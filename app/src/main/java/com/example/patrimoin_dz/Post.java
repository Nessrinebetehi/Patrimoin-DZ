package com.example.patrimoin_dz;

public class Post {
    private String postId;
    private String userId;
    private String userName;
    private String userProfileImageUrl;
    private String imageUrl;
    private String content;
    private long timestamp;
    private boolean isPublic;

    // Constructeur sans argument requis par Firestore
    public Post() {
    }

    // Constructeur complet
    public Post(String postId, String userId, String userName, String userProfileImageUrl, String imageUrl, String content, long timestamp) {
        this.postId = postId;
        this.userId = userId;
        this.userName = userName;
        this.userProfileImageUrl = userProfileImageUrl;
        this.imageUrl = imageUrl;
        this.content = content;
        this.timestamp = timestamp;
        this.isPublic = false; // Par défaut
    }

    // Getters
    public String getPostId() {
        return postId;
    }

    public String getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }

    public String getUserProfileImageUrl() {
        return userProfileImageUrl;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getContent() {
        return content;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public boolean isPublic() {
        return isPublic;
    }

    // Setters
    public void setPostId(String postId) {
        this.postId = postId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setUserProfileImageUrl(String userProfileImageUrl) {
        this.userProfileImageUrl = userProfileImageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public void setIsPublic(boolean isPublic) {
        this.isPublic = isPublic;
    }
}