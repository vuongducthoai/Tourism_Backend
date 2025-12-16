package com.tourism.backend.dto.chatbot;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VectorDocumentDTO {
    private String id;              // ID duy nhất trên Pinecone (vd: "tour_123")
    private String content;         // Nội dung văn bản mô tả Tour (để Gemini đọc)
    private String type;            // Loại: TOUR, LOCATION, REVIEW...
    private Integer entityId;       // ID gốc trong SQL (TourID)

    // Đây là cái quan trọng nhất: Dãy số Vector 768 chiều
    private List<Float> embedding;
    private Float score;
    private String metadata;        // Các thông tin phụ lưu dạng JSON String (Tên, Giá...)
}