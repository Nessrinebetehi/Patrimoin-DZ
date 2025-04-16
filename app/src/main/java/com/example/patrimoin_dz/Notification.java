package com.example.patrimoin_dz;

public class Notification {
    private String message;
    private String time;
    private String requestId;

    public Notification(String message, String time, String requestId) {
        this.message = message;
        this.time = time;
        this.requestId = requestId;
    }

    public String getMessage() {
        return message;
    }

    public String getTime() {
        return time;
    }

    public String getRequestId() {
        return requestId;
    }
}