package com.example.patrimoin_dz;

import com.google.gson.annotations.SerializedName;

public class GrokResponse {
    @SerializedName("choices")
    private Choice[] choices;

    public String getResponseText() {
        if (choices != null && choices.length > 0) {
            return choices[0].getMessage().getContent();
        }
        return "No response received.";
    }

    public static class Choice {
        @SerializedName("message")
        private Message message;

        public Message getMessage() {
            return message;
        }
    }

    public static class Message {
        @SerializedName("content")
        private String content;

        public String getContent() {
            return content;
        }
    }
}