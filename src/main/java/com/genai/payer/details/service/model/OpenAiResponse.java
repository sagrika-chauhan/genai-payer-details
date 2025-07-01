package com.genai.payer.details.service.model;

import java.util.Map;

public class OpenAiResponse {
    private Map<String, String> prediction;
    private String explanation;

    // Parameterized constructor
    public OpenAiResponse(String explanation) {
        this.explanation = explanation;
    }

    // Getter for prediction
    public Map<String, String> getPrediction() {
        return prediction;
    }

    // Setter for prediction
    public void setPrediction(Map<String, String> prediction) {
        this.prediction = prediction;
    }

    // Getter for explanation
    public String getExplanation() {
        return explanation;
    }

    // Setter for explanation
    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }
}
