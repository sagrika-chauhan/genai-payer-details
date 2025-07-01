package com.genai.payer.details.loader;



import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class DataLoader {
    private List<Map<String, String>> records = new ArrayList<>();

    @PostConstruct
    public void load() {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(
                getClass().getResourceAsStream("/decision_data.csv")))) {
            String[] headers = br.readLine().split(",");
            System.out.println("[DataLoader] CSV headers: " + Arrays.toString(headers));
            String line;
            int rowCount = 0;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",", -1); // -1 to keep trailing empty strings
                Map<String, String> record = new HashMap<>();
                for (int i = 0; i < headers.length && i < values.length; i++) {
                    record.put(headers[i].trim(), values[i].trim());
                }
                records.add(record);
                rowCount++;
                // Log each row (optionally limit to first few)
                if (rowCount <= 5) {
                    System.out.println("[DataLoader] Row " + rowCount + ": " + record);
                }
            }
            System.out.println("[DataLoader] Loaded total rows: " + rowCount);
        } catch (Exception e) {
            System.out.println("[DataLoader] Error reading CSV: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public List<Map<String, String>> findSimilar(Map<String, String> queryFields, int limit) {
        // Simple retrieval: return records that match all provided fields (non-empty in query)
        List<Map<String, String>> result = records.stream()
                .filter(record -> queryFields.entrySet().stream()
                        .allMatch(e -> e.getValue().isEmpty()
                                || record.getOrDefault(e.getKey(), "").equalsIgnoreCase(e.getValue())))
                .limit(limit)
                .collect(Collectors.toList());
        System.out.println("[DataLoader] Query: " + queryFields);
        System.out.println("[DataLoader] Found " + result.size() + " similar records.");
        return result;
    }

    public List<Map<String, String>> getTruthData(int count) {
        return records.stream().limit(count).collect(Collectors.toList());
    }
}