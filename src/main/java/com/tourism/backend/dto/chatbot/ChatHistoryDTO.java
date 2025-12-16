package com.tourism.backend.dto.chatbot;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatHistoryDTO {
    private String sessionId;
    private Integer userId;
    private String userMessage;
    private String botResponse;
    private LocalDateTime timestamp;
}