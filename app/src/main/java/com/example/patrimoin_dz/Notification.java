package com.example.patrimoin_dz;

public class Notification {
    private String message;
    private String time;

    public Notification(String message, String time) {
        this.message = message;
        this.time = time;
    }

    public String getMessage() {
        return message;
    }

    public String getTime() {
        return time;
    }
}