package com.example.patrimoin_dz;

public class Share {
    private String senderId; // ID de l'utilisateur qui partage
    private String postId;   // ID de la publication partagée

    public Share() {
    }

    public Share(String senderId, String postId) {
        this.senderId = senderId;
        this.postId = postId;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }
}