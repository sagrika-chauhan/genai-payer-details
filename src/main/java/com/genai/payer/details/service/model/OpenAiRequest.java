package com.genai.payer.details.service.model;


public class OpenAiRequest {
    private String prompt;

    public OpenAiRequest() {}

    public OpenAiRequest(String prompt) {
        this.prompt = prompt;
    }

    public String getPrompt() {
        return prompt;
    }

    public void setPrompt(String prompt) {
        this.prompt = prompt;
    }
}