package com.example.patrimoin_dz;

public class Friend {
    private String friendId;
    private String username;

    public Friend() {
    }

    public Friend(String friendId, String username) {
        this.friendId = friendId;
        this.username = username;
    }

    public String getFriendId() {
        return friendId;
    }

    public void setFriendId(String friendId) {
        this.friendId = friendId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserId() {
        return friendId;
    }
}