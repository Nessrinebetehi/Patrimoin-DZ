package com.example.patrimoin_dz;

import java.io.Serializable;

public class Story implements Serializable {
    private String storyId;
    private String userId; // Remplacer username par userId pour correspondre à Firestore
    private String mediaUrl;
    private String text;
    private String musicUrl;
    private long timestamp;

    public Story() {
    }

    public Story(String storyId, String userId, String mediaUrl, String text, String musicUrl, long timestamp) {
        this.storyId = storyId;
        this.userId = userId;
        this.mediaUrl = mediaUrl;
        this.text = text;
        this.musicUrl = musicUrl;
        this.timestamp = timestamp;
    }

    public String getStoryId() {
        return storyId;
    }

    public void setStoryId(String storyId) {
        this.storyId = storyId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getMediaUrl() {
        return mediaUrl;
    }

    public void setMediaUrl(String mediaUrl) {
        this.mediaUrl = mediaUrl;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getMusicUrl() {
        return musicUrl;
    }

    public void setMusicUrl(String musicUrl) {
        this.musicUrl = musicUrl;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    // Supprimer getName() car il n'est pas utilisé correctement
    // public int getName() { return 0; }

    // Déprécier l'ancien getter pour username si nécessaire
    @Deprecated
    public String getUsername() {
        return userId;
    }

    @Deprecated
    public void setUsername(String username) {
        this.userId = username;
    }

    public int getName() {
        return 0;
    }
}