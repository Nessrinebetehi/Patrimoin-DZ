package com.example.patrimoin_dz;

public class Chat {
    private String userName;
    private String lastMessage;
    private int unreadCount;
    private String timestamp;
    private int profileImage;

    public Chat(String userName, String lastMessage, int unreadCount, String timestamp, int profileImage) {
        this.userName = userName;
        this.lastMessage = lastMessage;
        this.unreadCount = unreadCount;
        this.timestamp = timestamp;
        this.profileImage = profileImage;
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

    public CharSequence getTimestamp() {
        return timestamp;
    }

    public int getProfileImage() {
        return profileImage;
    }

    public Object getConversationId() {
        return null;
    }
}