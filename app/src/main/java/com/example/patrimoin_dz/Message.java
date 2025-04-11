package com.example.patrimoin_dz;

import android.net.Uri;

public class Message {
    private String text;
    private boolean isSentByUser;
    private Uri imageUri; // Pour stocker l'URI de l'image
    private boolean isImage; // Indique si le message est une image

    // Constructeur pour un message texte
    public Message(String text, boolean isSentByUser) {
        this.text = text;
        this.isSentByUser = isSentByUser;
        this.isImage = false;
        this.imageUri = null;
    }

    // Constructeur pour un message avec image
    public Message(Uri imageUri, boolean isSentByUser) {
        this.text = "";
        this.isSentByUser = isSentByUser;
        this.imageUri = imageUri;
        this.isImage = true;
    }

    public String getText() {
        return text;
    }

    public boolean isSentByUser() {
        return isSentByUser;
    }

    public Uri getImageUri() {
        return imageUri;
    }

    public boolean isImage() {
        return isImage;
    }
}