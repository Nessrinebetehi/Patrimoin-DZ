package com.example.patrimoin_dz;

public class Post {
    private String postId; // Ajout de l'ID de la publication
    private String userId;
    private String imageUrl;
    private long timestamp;

    public Post(String userId, String string, long l, String content) {
    }

    public Post(String userId, String imageUrl, long timestamp) {
        this.userId = userId;
        this.imageUrl = imageUrl;
        this.timestamp = timestamp;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}