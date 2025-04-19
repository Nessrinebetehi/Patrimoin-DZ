package com.example.patrimoin_dz;

public class Chat {
    private String userName;
    private String lastMessage;
    private int unreadCount;
    private String timestamp;
    private String profileImageUrl;

    public Chat(String userName, String lastMessage, int unreadCount, String timestamp, String profileImageUrl) {
        this.userName = userName;
        this.lastMessage = lastMessage;
        this.unreadCount = unreadCount;
        this.timestamp = timestamp;
        this.profileImageUrl = profileImageUrl;
    }

    public String getUserName() {
        return userName;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public int getUnreadCount() {
        return unreadCount;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public String getConversationId() {
        return null; // À ajuster si tu veux passer un conversationId spécifique
    }
}