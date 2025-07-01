package com.genai.payer.details.controller;

import com.genai.payer.details.loader.DataLoader;
import com.genai.payer.details.service.OpenAiService;
import com.genai.payer.details.service.model.OpenAiRequest;
import com.genai.payer.details.service.model.OpenAiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/openai")
public class OpenAiController {
    @Autowired
    private OpenAiService openAiService;
    @Autowired
    private DataLoader dataLoader;

    @PostMapping("/predict")
    public OpenAiResponse handlePrediction(@RequestBody Map<String, String> queryFields) {
        // 1. Retrieve similar records
        List<Map<String, String>> contextRecords = dataLoader.findSimilar(queryFields, 3);

        // 2. Build prompt
        StringBuilder prompt = new StringBuilder(
                "You are a business rule assistant. Based on the following similar booking records, infer missing fields for a new entry.\n\n"
        );
        for (int i = 0; i < contextRecords.size(); i++) {
            prompt.append((i + 1)).append(". ");
            for (Map.Entry<String, String> entry : contextRecords.get(i).entrySet()) {
                if (!entry.getValue().isEmpty())
                    prompt.append(entry.getKey()).append(": ").append(entry.getValue()).append(", ");
            }
            prompt.append("\n");
        }
        prompt.append("\nGiven: ");
        queryFields.forEach((k, v) -> prompt.append(k).append(": ").append(v).append(", "));
        prompt.append(
                "\n\nPredict the most likely values for: Payment Term, Invoice Party, Credit Party, CBU, CBU ID.\n" +
                        "Return your answer as a JSON object with two fields:\n" +
                        "- \"prediction\": the predicted values\n" +
                        "- \"explanation\": a brief explanation of how you arrived at your answer (e.g., which records or patterns influenced your choice)\n"
        );

        // 3. Call LLM
        return openAiService.getOpenAiResponse(new OpenAiRequest(prompt.toString()));
    }
}