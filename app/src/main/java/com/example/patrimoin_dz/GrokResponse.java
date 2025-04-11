package com.example.patrimoin_dz;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class GrokResponse {
    @SerializedName("candidates")
    private List<Candidate> candidates;

    public String getResponseText() {
        if (candidates != null && !candidates.isEmpty()) {
            Candidate candidate = candidates.get(0);
            Content content = candidate.getContent();
            if (content != null && content.getParts() != null && !content.getParts().isEmpty()) {
                return content.getParts().get(0).getText();
            }
        }
        return "Aucune réponse générée.";
    }

    public static class Candidate {
        @SerializedName("content")
        private Content content;

        public Content getContent() {
            return content;
        }
    }

    public static class Content {
        @SerializedName("parts")
        private List<Part> parts;

        public List<Part> getParts() {
            return parts;
        }
    }

    public static class Part {
        @SerializedName("text")
        private String text;

        public String getText() {
            return text;
        }
    }
}