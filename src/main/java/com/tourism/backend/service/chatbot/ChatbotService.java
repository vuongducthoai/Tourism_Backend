package com.tourism.backend.service.chatbot;

import com.google.gson.Gson;
import com.tourism.backend.dto.chatbot.*;
import com.tourism.backend.entity.Tour;
import com.tourism.backend.entity.TourDeparture;
import com.tourism.backend.entity.TourImage;
import com.tourism.backend.enums.PassengerType;
import com.tourism.backend.repository.TourDepartureRepository;
import com.tourism.backend.repository.TourRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatbotService {

    private final VectorService vectorService;
    private final TourRepository tourRepository;
    private final TourDepartureRepository tourDepartureRepository;
    private final RestTemplate restTemplate = new RestTemplate();
    private final Gson gson = new Gson();

    @Value("${gemini.api.key}")
    private String geminiApiKey;

    @Value("${chatbot.generation.model}")
    private String generationModel;

    private static final String GEMINI_API_URL =
            "https://generativelanguage.googleapis.com/v1beta/models/";

    public ChatMessageResponse handleUserMessage(ChatMessageRequest request) {
        try {
            log.info("📩 Received message: {}", request.getMessage());

            List<VectorDocumentDTO> relevantDocs = vectorService.searchSimilar(
                    request.getMessage(), 10
            );

            // ✅ Truyền userMessage để context có thể lọc và sắp xếp
            String context = buildEnhancedContext(relevantDocs, request.getMessage());
            String prompt = buildEnhancedPrompt(request.getMessage(), context);
            String aiResponse = callGeminiAPI(prompt);

            Map<String, List<Integer>> entityIds = parseEntityIds(relevantDocs);
            List<ChatMessageResponse.TourSuggestion> suggestions =
                    buildEnhancedTourSuggestions(entityIds, relevantDocs);
            List<ChatMessageResponse.QuickAction> quickActions = buildQuickActions(request);

            return ChatMessageResponse.builder()
                    .reply(aiResponse)
                    .tourSuggestions(suggestions)
                    .quickActions(quickActions)
                    .sessionId(request.getSessionId())
                    .timestamp(LocalDateTime.now())
                    .build();

        } catch (Exception e) {
            log.error(" Error handling message", e);
            return ChatMessageResponse.builder()
                    .reply("Xin lỗi, tôi đang gặp sự cố. Vui lòng thử lại sau.")
                    .tourSuggestions(new ArrayList<>())
                    .quickActions(new ArrayList<>())
                    .sessionId(request.getSessionId())
                    .timestamp(LocalDateTime.now())
                    .build();
        }
    }

    /**
     * ✅ BUILD CONTEXT - HIỂN THỊ LOCATIONID CHO LOCATION
     */
    private String buildEnhancedContext(List<VectorDocumentDTO> docs, String userMessage) {
        if (docs.isEmpty()) {
            return "Không tìm thấy thông tin liên quan trong hệ thống.";
        }

        // ✅ Phát hiện câu hỏi về giảm giá/coupon
        boolean isDiscountQuery = userMessage.toLowerCase().matches(".*(giảm\\s*(giá|sâu)|ưu\\s*đãi|khuyến\\s*mãi|coupon|mã\\s*giảm).*");

        List<VectorDocumentDTO> filteredDocs = docs;

        // ✅ Nếu hỏi về giảm giá, CHỈ LẤY các tour có couponDiscount
        if (isDiscountQuery) {
            filteredDocs = docs.stream()
                    .filter(doc -> {
                        if (!"TOUR_DEPARTURE".equals(doc.getType())) {
                            return false;
                        }
                        try {
                            Map<String, Object> metadata = gson.fromJson(doc.getMetadata(), Map.class);
                            // Chỉ lấy tour có couponDiscount > 0
                            return metadata.containsKey("couponDiscount")
                                    && ((Number) metadata.get("couponDiscount")).doubleValue() > 0;
                        } catch (Exception e) {
                            return false;
                        }
                    })
                    .sorted((d1, d2) -> {
                        // Sắp xếp theo couponDiscount từ cao đến thấp
                        double discount1 = extractCouponDiscount(d1);
                        double discount2 = extractCouponDiscount(d2);
                        return Double.compare(discount2, discount1);
                    })
                    .collect(Collectors.toList());

            // Nếu không tìm thấy tour nào có coupon
            if (filteredDocs.isEmpty()) {
                log.warn("⚠️ Không tìm thấy tour nào có coupon discount");
                // Vẫn trả về docs gốc nhưng đánh dấu
                filteredDocs = docs;
            } else {
                log.info("✅ Tìm thấy {} tour có coupon discount", filteredDocs.size());
            }
        }

        StringBuilder context = new StringBuilder();
        context.append("Dữ liệu từ hệ thống");

        if (isDiscountQuery && filteredDocs.stream().anyMatch(d -> {
            try {
                Map<String, Object> metadata = gson.fromJson(d.getMetadata(), Map.class);
                return metadata.containsKey("couponDiscount")
                        && ((Number) metadata.get("couponDiscount")).doubleValue() > 0;
            } catch (Exception e) {
                return false;
            }
        })) {
            context.append(" (các tour có mã giảm giá coupon, sắp xếp theo mức giảm từ cao đến thấp)");
        }

        context.append(":\n\n");

        for (int i = 0; i < filteredDocs.size(); i++) {
            VectorDocumentDTO doc = filteredDocs.get(i);
            context.append(i + 1).append(". ").append(doc.getContent()).append("\n");

            try {
                Map<String, Object> metadata = gson.fromJson(doc.getMetadata(), Map.class);

                if ("TOUR_DEPARTURE".equals(doc.getType())) {
                    double salePrice = ((Number) metadata.getOrDefault("salePrice", 0)).doubleValue();
                    double originalPrice = ((Number) metadata.getOrDefault("originalPrice", salePrice)).doubleValue();

                    context.append("   [Mã tour: ").append(metadata.get("tourCode"))
                            .append(", Ngày: ").append(metadata.get("departureDate"))
                            .append(", Giá ADULT: ").append(String.format("%,.0f", salePrice))
                            .append(" VND");

                    // ✅ QUAN TRỌNG: Kiểm tra và hiển thị ĐÚNG loại giảm giá
                    if (metadata.containsKey("couponDiscount")) {
                        double couponDiscount = ((Number) metadata.get("couponDiscount")).doubleValue();
                        if (couponDiscount > 0) {
                            // ✅ Tour CÓ COUPON - Hiển thị Mã giảm giá
                            context.append(", Giá gốc: ").append(String.format("%,.0f", originalPrice))
                                    .append(" VND")
                                    .append(", Mã giảm giá (COUPON): ").append(String.format("%,.0f", couponDiscount))
                                    .append(" VND");
                        }
                    } else {
                        // ✅ Tour KHÔNG CÓ COUPON - Chỉ hiển thị giảm giá thông thường
                        double normalDiscount = originalPrice - salePrice;
                        if (normalDiscount > 0) {
                            context.append(", Giá gốc: ").append(String.format("%,.0f", originalPrice))
                                    .append(" VND")
                                    .append(", Giảm giá thông thường: ").append(String.format("%,.0f", normalDiscount))
                                    .append(" VND");
                        }
                    }

                    context.append("]\n");
                }
                else if ("LOCATION".equals(doc.getType())) {
                    Object locationIdObj = metadata.get("locationID");
                    String locationName = (String) metadata.get("locationName");

                    if (locationIdObj != null) {
                        int locationId = ((Number) locationIdObj).intValue();
                        context.append("   [Địa điểm: ").append(locationName)
                                .append(", LocationID: ").append(locationId)
                                .append("]\n");
                    }
                }

            } catch (Exception e) {
                log.warn("⚠️ Error parsing metadata for doc: {}", doc.getId(), e);
            }

            context.append("\n");
        }

        return context.toString();
    }

    // ✅ Hàm phụ trợ: Trích xuất couponDiscount
    private double extractCouponDiscount(VectorDocumentDTO doc) {
        try {
            Map<String, Object> metadata = gson.fromJson(doc.getMetadata(), Map.class);

            if ("TOUR_DEPARTURE".equals(doc.getType()) && metadata.containsKey("couponDiscount")) {
                double discount = ((Number) metadata.get("couponDiscount")).doubleValue();
                log.debug("📊 Doc {} has coupon discount: {}", doc.getId(), discount);
                return discount;
            }
        } catch (Exception e) {
            log.warn("⚠️ Error extracting coupon discount from doc: {}", doc.getId(), e);
        }
        return 0.0;
    }

    /**
     * ✅ PROMPT - HƯỚNG DẪN AI TẠO LINK CHÍNH XÁC
     */
    private String buildEnhancedPrompt(String userMessage, String context) {
        return String.format("""
                Bạn là Trợ lý Du lịch AI chuyên nghiệp, hiện đại và thân thiện của hệ thống Tourism.
            
            🔹 NHIỆM VỤ PHÂN TÍCH DỮ LIỆU:
            1. **Giá:** Luôn dùng "Giá ADULT" (người lớn) làm chuẩn.
            2. **Giảm giá/Khuyến mãi:**
              - Khi người dùng hỏi "giảm giá", "giảm sâu", "ưu đãi", "khuyến mãi", "coupon", "mã giảm giá":
                * CHỈ giới thiệu các tour có "Mã giảm giá" (coupon discount trong context)
                * Sắp xếp theo mức "Mã giảm giá" từ cao đến thấp
                * Ưu tiên tour có mức giảm giá coupon lớn nhất
              - Trong Context, tour có coupon sẽ hiển thị: "Mã giảm giá: X VND"
              - Tour không có coupon sẽ hiển thị: "Giảm: X VND" (không đề cập trong trường hợp này)
            3. **Đánh giá:** Chỉ đề xuất tour có Rating >= 4.0 sao nếu khách hỏi về chất lượng.
            4. **Thời gian:** Ưu tiên các ngày khởi hành gần nhất so với hiện tại.
            
            🔹 QUY TẮC LINK (TUYỆT ĐỐI TUÂN THỦ):
            
            **A. Link Tour (Có Mã tour trong Context):**
            - Format: **[Xem chi tiết](/tour/TOUR-CODE)**
            - VÍ DỤ: Nếu context có "Mã tour: TOUR-HG-04" → Viết: **[Xem chi tiết](/tour/TOUR-HG-04)**
            - ❌ KHÔNG viết: /tour/TOUR-HG-04 (thiếu Markdown)
       
            
            **B. Link Địa điểm mà liên quan đến điểm khởi hành , điểm bắt đầu (Có LocationID trong Context):**
            - Format: **[Khám phá ngay](/tours?startLocationID=LOCATION_ID)**
            - VÍ DỤ: Nếu context có "LocationID: 5" → Viết: **[Khám phá ngay](/tours?startLocationID=5)**
            - ✅ LẤY LocationID TỪ CONTEXT: Trong dấu [...] sẽ có "LocationID: X"
            - ❌ KHÔNG tự bịa số, phải dùng số từ context
          
            **C. Link Địa điểm mà liên quan đến điểm đến , nơi muốn đến (Có LocationID trong Context):**
            - Format: **[Khám phá ngay](/tours?endLocationID=LOCATION_ID)**
            - VÍ DỤ: Nếu context có "LocationID: 5" → Viết: **[Khám phá ngay](/tours?endLocationID=5)**
            - ✅ LẤY LocationID TỪ CONTEXT: Trong dấu [...] sẽ có "LocationID: X"
            - ❌ KHÔNG tự bịa số, phải dùng số từ context
            
            **D. Nếu KHÔNG có Mã tour hoặc LocationID:**
            - Không chèn link, chỉ gợi ý tìm kiếm: "Bạn có thể tìm thêm các tour khác trên hệ thống."
            
            🔹 FORMAT VĂN BẢN (STYLE HIỆN ĐẠI & GỌN GÀNG):
            - **Không xuống dòng kép** giữa các thông tin của cùng một tour.
            - Khoảng cách giữa các đoạn không lớn.
            - Sử dụng icon để làm nổi bật thay vì gạch đầu dòng và point.
            - **In đậm** tên Tour/Địa điểm và Giá tiền.
            - Cấu trúc mong muốn:
               
               **[Tên Tour]**
               [Thời lượng] | [Ngày đi gần nhất]
               Giá: (hiển thị giá gốc originalPrice trong Context) [Nếu có: Giảm X%%]
               **[Xem chi tiết](/tour/TOUR-CODE)**
            
            - Giọng văn: Ngắn gọn, súc tích, thân thiện.
            
            === DỮ LIỆU HỆ THỐNG (CONTEXT) ===
            %s
            
            === CÂU HỎI KHÁCH HÀNG ===
            "%s"
            
            === TRẢ LỜI CỦA BẠN (Markdown) ===
            """, context, userMessage);
    }

    private Map<String, List<Integer>> parseEntityIds(List<VectorDocumentDTO> docs) {
        Map<String, List<Integer>> result = new HashMap<>();

        result.put("tours", docs.stream()
                .filter(d -> "TOUR_SUMMARY".equals(d.getType()))
                .map(VectorDocumentDTO::getEntityId)
                .distinct()
                .limit(3)
                .collect(Collectors.toList()));

        result.put("departures", docs.stream()
                .filter(d -> "TOUR_DEPARTURE".equals(d.getType()))
                .map(VectorDocumentDTO::getEntityId)
                .distinct()
                .limit(5)
                .collect(Collectors.toList()));

        return result;
    }

    private List<ChatMessageResponse.TourSuggestion> buildEnhancedTourSuggestions(
            Map<String, List<Integer>> entityIds,
            List<VectorDocumentDTO> docs
    ) {
        List<ChatMessageResponse.TourSuggestion> suggestions = new ArrayList<>();

        List<Integer> departureIds = entityIds.get("departures");
        if (departureIds != null && !departureIds.isEmpty()) {
            List<TourDeparture> departures = tourDepartureRepository.findAllById(departureIds);

            for (TourDeparture dep : departures) {
                Tour tour = dep.getTour();
                if (tour == null) continue;

                suggestions.add(buildSuggestionFromDeparture(dep, tour, docs));
            }
        }

        if (suggestions.size() < 3) {
            List<Integer> tourIds = entityIds.get("tours");
            if (tourIds != null) {
                List<Tour> tours = tourRepository.findAllById(tourIds);

                for (Tour tour : tours) {
                    if (suggestions.size() >= 3) break;
                    suggestions.add(buildSuggestionFromTour(tour, docs));
                }
            }
        }

        return suggestions.stream().limit(3).collect(Collectors.toList());
    }

    private ChatMessageResponse.TourSuggestion buildSuggestionFromDeparture(
            TourDeparture departure,
            Tour tour,
            List<VectorDocumentDTO> docs
    ) {
        String imageUrl = tour.getImages().stream()
                .filter(TourImage::getIsMainImage)
                .findFirst()
                .map(TourImage::getImageURL)
                .orElse(null);

        Double price = departure.getPricings().stream()
                .filter(p -> p.getPassengerType() == PassengerType.ADULT)
                .findFirst()
                .map(p -> p.getSalePrice().doubleValue())
                .orElse(0.0);

        Float score = docs.stream()
                .filter(d -> "TOUR_DEPARTURE".equals(d.getType()) && d.getEntityId().equals(departure.getDepartureID()))
                .findFirst()
                .map(VectorDocumentDTO::getScore)
                .orElse(0.8f);

        return ChatMessageResponse.TourSuggestion.builder()
                .tourId(tour.getTourID())
                .tourCode(tour.getTourCode())
                .tourName(tour.getTourName())
                .imageUrl(imageUrl)
                .minPrice(price)
                .duration(tour.getDuration())
                .detailUrl("/tour/" + tour.getTourCode()) // ✅ /tour/ (không có s)
                .relevanceScore(score.doubleValue())
                .build();
    }

    private ChatMessageResponse.TourSuggestion buildSuggestionFromTour(
            Tour tour,
            List<VectorDocumentDTO> docs
    ) {
        String imageUrl = tour.getImages().stream()
                .filter(TourImage::getIsMainImage)
                .findFirst()
                .map(TourImage::getImageURL)
                .orElse(null);

        Double minPrice = tour.getDepartures().stream()
                .flatMap(dep -> dep.getPricings().stream())
                .filter(p -> p.getPassengerType() == PassengerType.ADULT)
                .map(p -> p.getSalePrice().doubleValue())
                .min(Double::compareTo)
                .orElse(0.0);

        Float score = docs.stream()
                .filter(d -> d.getEntityId().equals(tour.getTourID()))
                .findFirst()
                .map(VectorDocumentDTO::getScore)
                .orElse(0.7f);

        return ChatMessageResponse.TourSuggestion.builder()
                .tourId(tour.getTourID())
                .tourCode(tour.getTourCode())
                .tourName(tour.getTourName())
                .imageUrl(imageUrl)
                .minPrice(minPrice)
                .duration(tour.getDuration())
                .detailUrl("/tour/" + tour.getTourCode()) // ✅ /tour/ (không có s)
                .relevanceScore(score.doubleValue())
                .build();
    }

    private String callGeminiAPI(String prompt) {
        try {
            String url = GEMINI_API_URL + generationModel + ":generateContent?key=" + geminiApiKey;

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("contents", List.of(
                    Map.of("parts", List.of(Map.of("text", prompt)))
            ));

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);
            ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                return extractTextFromGeminiResponse(response.getBody());
            }

        } catch (Exception e) {
            log.error("Error calling Gemini API", e);
        }
        return "Xin lỗi, tôi không thể xử lý yêu cầu của bạn lúc này.";
    }

    private String extractTextFromGeminiResponse(Map<String, Object> responseBody) {
        try {
            List<Map<String, Object>> candidates =
                    (List<Map<String, Object>>) responseBody.get("candidates");

            if (candidates != null && !candidates.isEmpty()) {
                Map<String, Object> content =
                        (Map<String, Object>) candidates.get(0).get("content");
                List<Map<String, Object>> parts =
                        (List<Map<String, Object>>) content.get("parts");

                if (parts != null && !parts.isEmpty()) {
                    return (String) parts.get(0).get("text");
                }
            }
        } catch (Exception e) {
            log.error("Error parsing Gemini response", e);
        }
        return "Lỗi khi xử lý phản hồi.";
    }

    private List<ChatMessageResponse.QuickAction> buildQuickActions(ChatMessageRequest request) {
        List<ChatMessageResponse.QuickAction> actions = new ArrayList<>();
        String message = request.getMessage().toLowerCase();

        if (message.contains("giảm giá") || message.contains("khuyến mãi") || message.contains("ưu đãi")) {
            actions.add(ChatMessageResponse.QuickAction.builder()
                    .label("💰 Tours giảm giá sốc")
                    .action("VIEW_DEALS")
                    .url("/tour?filter=discount")
                    .build());
        }

        if (message.contains("yêu thích") || message.contains("đánh giá cao")) {
            actions.add(ChatMessageResponse.QuickAction.builder()
                    .label("⭐ Tours được yêu thích")
                    .action("VIEW_FAVORITES")
                    .url("/tour?sort=rating")
                    .build());
        }

        if (message.contains("gần") || message.contains("sớm") || message.contains("sắp")) {
            actions.add(ChatMessageResponse.QuickAction.builder()
                    .label("📅 Khởi hành gần nhất")
                    .action("VIEW_UPCOMING")
                    .url("/tour?sort=date")
                    .build());
        }

        return actions;
    }
}