package com.example.patrimoin_dz;

import android.net.Uri;

public class Message {
    private String id;
    private String content;
    private String senderId;
    private long timestamp;
    private boolean isImage;

    // Constructeur sans argument requis par Firestore
    public Message(Uri uri, boolean b) {
    }

    public Message(String content, boolean isImage) {
        this.content = content;
        this.senderId = senderId;
        this.timestamp = timestamp;
        this.isImage = isImage;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public boolean isImage() {
        return isImage;
    }

    public void setImage(boolean isImage) {
        this.isImage = isImage;
    }
}