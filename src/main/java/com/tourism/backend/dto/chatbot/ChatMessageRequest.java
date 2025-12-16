package com.tourism.backend.dto.chatbot;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatMessageRequest {
    private String message;     // Nội dung khách chat: "Có tour đi Vĩnh Long không?"
    private String sessionId;   // ID phiên chat (để bot nhớ ngữ cảnh)
    private Integer userId;     // ID khách hàng (nếu đã đăng nhập)
}