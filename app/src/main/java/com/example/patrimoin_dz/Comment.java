package com.example.patrimoin_dz;

import java.util.ArrayList;
import java.util.List;

public class Comment {
    private String commentId; // Ajout de l'ID du commentaire
    private String userId;
    private String userName;
    private String text;
    private String time;
    private String reaction;
    private List<Comment> replies;

    public Comment() {
        this.replies = new ArrayList<>();
    }

    public Comment(String userId, String userName, String text, String time) {
        this.userId = userId;
        this.userName = userName;
        this.text = text;
        this.time = time;
        this.replies = new ArrayList<>();
    }

    // Getters et setters
    public String getCommentId() {
        return commentId;
    }

    public void setCommentId(String commentId) {
        this.commentId = commentId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getReaction() {
        return reaction;
    }

    public void setReaction(String reaction) {
        this.reaction = reaction;
    }

    public List<Comment> getReplies() {
        return replies;
    }

    public void setReplies(List<Comment> replies) {
        this.replies = replies;
    }
}