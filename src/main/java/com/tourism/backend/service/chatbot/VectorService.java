package com.tourism.backend.service.chatbot;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.tourism.backend.dto.chatbot.VectorDocumentDTO;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class VectorService {

    @Value("${chatbot.vector-db.pinecone.api-key}")
    private String pineconeApiKey;

    @Value("${chatbot.vector-db.pinecone.index-name}")
    private String indexName;

    @Value("${chatbot.vector-db.pinecone.host}")
    private String pineconeHost;

    @Value("${gemini.api.key}")
    private String geminiApiKey;

    @Value("${chatbot.embedding.model}")
    private String embeddingModel;

    private final RestTemplate restTemplate = new RestTemplate();
    private final Gson gson = new Gson();

    private String pineconeUrl;

    @PostConstruct
    public void initializePinecone() {
        // Pinecone Data Plane URL
        this.pineconeUrl = pineconeHost;
        log.info("✅ Pinecone initialized with URL: {}", pineconeUrl);
    }

    // --- CREATE EMBEDDING (Google Gemini) ---
    public List<Float> createEmbedding(String text) {
        try {
            String url = "https://generativelanguage.googleapis.com/v1beta/models/"
                    + embeddingModel + ":embedContent?key=" + geminiApiKey;

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("content", Map.of("parts", List.of(Map.of("text", text))));

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);
            ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                Map<String, Object> body = response.getBody();
                Map<String, Object> embedding = (Map<String, Object>) body.get("embedding");
                List<Double> values = (List<Double>) embedding.get("values");
                return values.stream().map(Double::floatValue).collect(Collectors.toList());
            }
        } catch (Exception e) {
            log.error("Error creating embedding", e);
        }
        return new ArrayList<>();
    }

    // --- UPSERT VECTOR ---
    public void upsertVector(VectorDocumentDTO document) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Api-Key", pineconeApiKey);

            // Tạo vector object theo Pinecone API format
            Map<String, Object> vector = new HashMap<>();
            vector.put("id", document.getId());
            vector.put("values", document.getEmbedding());

            Map<String, Object> metadata = new HashMap<>();
            metadata.put("type", document.getType());
            metadata.put("entityId", String.valueOf(document.getEntityId()));
            metadata.put("content", document.getContent());
            metadata.put("metadata", document.getMetadata());
            vector.put("metadata", metadata);

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("vectors", Collections.singletonList(vector));

            String url = pineconeUrl + "/vectors/upsert";
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

            ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);

            log.info("✅ Upserted vector: {} - Response: {}", document.getId(), response.getStatusCode());

        } catch (Exception e) {
            log.error("❌ Error upserting vector: {}", e.getMessage(), e);
        }
    }

    // --- SEARCH SIMILAR VECTORS ---
    public List<VectorDocumentDTO> searchSimilar(String queryText, int topK) {
        try {
            List<Float> queryEmbedding = createEmbedding(queryText);
            if (queryEmbedding.isEmpty()) return new ArrayList<>();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Api-Key", pineconeApiKey);

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("vector", queryEmbedding);
            requestBody.put("topK", topK);
            requestBody.put("includeMetadata", true);
            requestBody.put("includeValues", false);

            String url = pineconeUrl + "/query";
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

            ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                Map<String, Object> body = response.getBody();
                List<Map<String, Object>> matches = (List<Map<String, Object>>) body.get("matches");

                return matches.stream()
                        .map(match -> {
                            Map<String, Object> metadata = (Map<String, Object>) match.get("metadata");

                            return VectorDocumentDTO.builder()
                                    .id((String) match.get("id"))
                                    .content(getStringFromMetadata(metadata, "content"))
                                    .type(getStringFromMetadata(metadata, "type"))
                                    .entityId(parseIntegerSafe(getStringFromMetadata(metadata, "entityId")))
                                    .metadata(getStringFromMetadata(metadata, "metadata"))
                                    .score(((Double) match.get("score")).floatValue())
                                    .build();
                        })
                        .collect(Collectors.toList());
            }

        } catch (Exception e) {
            log.error("❌ Error searching vectors: {}", e.getMessage(), e);
        }
        return new ArrayList<>();
    }

    // --- DELETE VECTORS BY FILTER ---
    public void deleteVectorsByEntityId(String type, Integer entityId) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Api-Key", pineconeApiKey);

            Map<String, Object> filter = new HashMap<>();
            filter.put("entityId", String.valueOf(entityId));
            filter.put("type", type);

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("filter", filter);
            requestBody.put("deleteAll", false);

            String url = pineconeUrl + "/vectors/delete";
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

            restTemplate.postForEntity(url, request, String.class);

            log.info("✅ Deleted vectors for {}:{}", type, entityId);

        } catch (Exception e) {
            log.error("❌ Error deleting vectors: {}", e.getMessage(), e);
        }
    }

    // --- HELPER METHODS ---
    private String getStringFromMetadata(Map<String, Object> metadata, String key) {
        if (metadata != null && metadata.containsKey(key)) {
            Object value = metadata.get(key);
            return value != null ? value.toString() : "";
        }
        return "";
    }

    private Integer parseIntegerSafe(String value) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    // --- DELETE ALL VECTORS (RESET DB) ---
    public void deleteAllVectors() {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Api-Key", pineconeApiKey);

            // Body: { "deleteAll": true }
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("deleteAll", true);

            String url = pineconeUrl + "/vectors/delete";
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

            restTemplate.postForEntity(url, request, String.class);

            log.info(" Đã xóa TOÀN BỘ dữ liệu trong Pinecone!");

        } catch (Exception e) {
            log.error(" Lỗi khi xóa toàn bộ vectors: {}", e.getMessage(), e);
        }
    }
}