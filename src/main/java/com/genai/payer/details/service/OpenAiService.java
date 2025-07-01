package com.genai.payer.details.service;



import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.genai.payer.details.service.model.OpenAiRequest;
import com.genai.payer.details.service.model.OpenAiResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class OpenAiService {

    @Value("${azure.openai.api.key}")
    private String apiKey;

    @Value("${azure.openai.endpoint}")
    private String endpoint;

    @Value("${azure.openai.deployment}")
    private String deployment;

    @Value("${azure.openai.api.version}")
    private String apiVersion;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public OpenAiResponse getOpenAiResponse(OpenAiRequest request) {
        // Azure OpenAI expects deployment name in the URL
        String url = String.format("%s/openai/deployments/%s/chat/completions?api-version=%s",
                endpoint, deployment, apiVersion);

        Map<String, Object> requestBody = new HashMap<>();
        // model is not required in Azure, deployment handles it
        List<Map<String, String>> messages = List.of(
                Map.of("role", "user", "content", request.getPrompt())
        );
        requestBody.put("messages", messages);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("api-key", apiKey);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<String> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                entity,
                String.class
        );

        String reply = "No response";
        try {
            JsonNode root = objectMapper.readTree(response.getBody());
            JsonNode choices = root.path("choices");
            if (choices.isArray() && choices.size() > 0) {
                reply = choices.get(0).path("message").path("content").asText();
            }
        } catch (Exception e) {
            reply = "Error parsing response: " + e.getMessage();
        }

        return new OpenAiResponse(reply);
    }
}