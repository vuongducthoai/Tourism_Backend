package com.tourism.backend.dto.chatbot;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatMessageResponse {
    private String reply;           // Lời thoại của Bot: "Dạ có 2 tour..."
    private List<TourSuggestion> tourSuggestions; // Danh sách thẻ Tour (Card)
    private List<QuickAction> quickActions;       // Các nút bấm gợi ý
    private String sessionId;
    private LocalDateTime timestamp;

    // Class con: Mô tả thông tin tóm tắt của 1 Tour để hiện lên Chatbox
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TourSuggestion {
        private Integer tourId;
        private String tourCode;
        private String tourName;
        private String imageUrl;    // Ảnh thumbnail
        private Double minPrice;    // Giá thấp nhất
        private String duration;    // 3N2Đ
        private String detailUrl;   // Link click vào xem chi tiết
        private Double relevanceScore; // Độ phù hợp (từ Vector Search)
    }

    // Class con: Các nút bấm nhanh (Ví dụ: "Xem khuyến mãi", "Đặt ngay")
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class QuickAction {
        private String label;   // Tên nút
        private String action;  // Hành động: SEARCH, LINK...
        private String url;     // Đường dẫn (nếu có)
    }
}