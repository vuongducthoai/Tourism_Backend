package com.tourism.backend.controller;

import com.tourism.backend.dto.chatbot.ChatMessageRequest;
import com.tourism.backend.dto.chatbot.ChatMessageResponse;
import com.tourism.backend.service.chatbot.ChatbotService;
import com.tourism.backend.service.chatbot.VectorService;
import com.tourism.backend.service.chatbot.VectorSyncService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/chatbot")
@RequiredArgsConstructor
public class ChatbotController {

    private final ChatbotService chatbotService;
    private final VectorSyncService vectorSyncService;
    private final VectorService vectorService;
    /**
     * API chính: Nhận tin nhắn từ user
     */
    @PostMapping("/chat")
    public ResponseEntity<ChatMessageResponse> chat(@RequestBody ChatMessageRequest request) {
        ChatMessageResponse response = chatbotService.handleUserMessage(request);
        return ResponseEntity.ok(response);
    }

    /**
     * API trigger sync manual (cho admin)
     */
    @PostMapping("/sync")
    public ResponseEntity<String> triggerSync() {
        vectorSyncService.syncAllTours();
        vectorSyncService.syncAllLocations();
        return ResponseEntity.ok("✅ Sync completed successfully!");
    }

    @DeleteMapping("/admin/clear-data")
    public ResponseEntity<String> clearAllData() {

        try {
            vectorService.deleteAllVectors();
            return ResponseEntity.ok("✅ Đã xóa sạch toàn bộ dữ liệu trên Pinecone.");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("❌ Lỗi: " + e.getMessage());
        }
    }
}