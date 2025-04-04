package com.example.patrimoin_dz;

import com.google.gson.annotations.SerializedName;

public class GrokRequest {
    @SerializedName("model")
    private String model = "grok-beta"; // Modèle par défaut

    @SerializedName("messages")
    private Message[] messages;

    public GrokRequest(String question) {
        this.messages = new Message[]{new Message("user", question)};
    }

    public static class Message {
        @SerializedName("role")
        private String role;

        @SerializedName("content")
        private String content;

        public Message(String role, String content) {
            this.role = role;
            this.content = content;
        }
    }
}