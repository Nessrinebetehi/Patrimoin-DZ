package com.example.patrimoin_dz;

import com.google.gson.annotations.SerializedName;
import java.util.ArrayList;
import java.util.List;

public class GrokRequest {
    @SerializedName("contents")
    private List<Content> contents = new ArrayList<>();

    public void setPrompt(String prompt) {
        Content content = new Content();
        Part part = new Part();
        part.setText(prompt);
        content.addPart(part);
        contents.add(content);
    }

    public static class Content {
        @SerializedName("parts")
        private List<Part> parts = new ArrayList<>();

        public void addPart(Part part) {
            parts.add(part);
        }
    }

    public static class Part {
        @SerializedName("text")
        private String text;

        public void setText(String text) {
            this.text = text;
        }
    }
}