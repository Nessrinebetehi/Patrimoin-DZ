package com.example.patrimoin_dz;

public class HistoryItem {
    private String title;
    private String preview;
    private String date;
    private String fullConversation;

    public HistoryItem(String title, String preview, String date, String fullConversation) {
        this.title = title;
        this.preview = preview;
        this.date = date;
        this.fullConversation = fullConversation;
    }

    public String getTitle() {
        return title;
    }

    public String getPreview() {
        return preview;
    }

    public String getDate() {
        return date;
    }

    public String getFullConversation() {
        return fullConversation;
    }
}