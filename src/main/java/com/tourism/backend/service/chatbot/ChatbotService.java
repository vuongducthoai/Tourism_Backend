package com.tourism.backend.service.chatbot;

import com.google.gson.Gson;
import com.tourism.backend.dto.chatbot.*;
import com.tourism.backend.entity.DepartureTransport;
import com.tourism.backend.entity.Tour;
import com.tourism.backend.entity.TourDeparture;
import com.tourism.backend.entity.TourImage;
import com.tourism.backend.enums.PassengerType;
import com.tourism.backend.enums.TransportType;
import com.tourism.backend.repository.TourDepartureRepository;
import com.tourism.backend.repository.TourRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
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

            // ✅ Phát hiện discount query để search thêm documents
            boolean isDiscountQuery = request.getMessage().toLowerCase()
                    .matches(".*(giảm\\s*(giá|sâu)|ưu\\s*đãi|khuyến\\s*mãi|coupon|mã\\s*giảm).*");

            // ✅ Tăng topK khi hỏi về discount/coupon để lấy thêm tour có coupon
            int topK = isDiscountQuery ? 50 : 10;
            log.info("🔍 Search with topK={}, isDiscountQuery={}", topK, isDiscountQuery);

            List<VectorDocumentDTO> relevantDocs = vectorService.searchSimilar(
                    request.getMessage(), topK
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
            // ✅ ĐẾM SỐ TOUR CÓ COUPON
            long tourWithCouponCount = filteredDocs.stream()
                    .filter(d -> {
                        try {
                            Map<String, Object> metadata = gson.fromJson(d.getMetadata(), Map.class);
                            return "TOUR_DEPARTURE".equals(d.getType())
                                    && metadata.containsKey("couponDiscount")
                                    && ((Number) metadata.get("couponDiscount")).doubleValue() > 0;
                        } catch (Exception e) {
                            return false;
                        }
                    })
                    .count();

            context.append(" - QUAN TRỌNG: Có CHÍNH XÁC ").append(tourWithCouponCount)
                    .append(" tour có mã giảm giá coupon (sắp xếp theo mức giảm từ cao đến thấp)");

            log.info("🎯 Tìm thấy {} tour có mã giảm giá coupon", tourWithCouponCount);
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
                    String tourName = (String) metadata.getOrDefault("tourName", "Unknown Tour");

                    context.append("   [Tên tour: ").append(tourName)
                            .append(", Mã tour: ").append(metadata.get("tourCode"))
                            .append(", Ngày: ").append(metadata.get("departureDate"))
                            .append(", Giá ADULT: ").append(String.format("%,.0f", salePrice))
                            .append(" VND");

                    // ✅ QUAN TRỌNG: Kiểm tra và hiển thị ĐÚNG loại giảm giá
                    if (metadata.containsKey("couponDiscount")) {
                        double couponDiscount = ((Number) metadata.get("couponDiscount")).doubleValue();
                        if (couponDiscount > 0) {
                            // ✅ Tour CÓ COUPON - Hiển thị Mã giảm giá với thông tin đầy đủ
                            context.append(", Giá gốc: ").append(String.format("%,.0f", originalPrice))
                                    .append(" VND")
                                    .append(", 🎁 MÃ GIẢM GIÁ ĐẶC BIỆT: ").append(String.format("%,.0f", couponDiscount))
                                    .append(" VND");

                            // Thêm thông tin coupon nếu có
                            if (metadata.containsKey("couponCode")) {
                                context.append(" (Mã: ").append(metadata.get("couponCode")).append(")");
                            }

                            log.info("📊 Tour {} có coupon giảm giá: {} VND", tourName, String.format("%,.0f", couponDiscount));
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
            
            2. **⚠️ QUY TẮC BẮT BUỘC KHI NGƯỜI DÙNG HỎI VỀ GIẢM GIÁ/COUPON:**
               
               🚨 TUYỆT ĐỐI PHẢI TUÂN THỦ:
               - Nếu Context có dòng "QUAN TRỌNG: Có CHÍNH XÁC X tour có mã giảm giá coupon"
               - BẠN PHẢI GIỚI THIỆU **TẤT CẢ X TOUR ĐÓ**, KHÔNG ĐƯỢC BỎ QUA BẤT KỲ TOUR NÀO!
               - KHÔNG ĐƯỢC chỉ giới thiệu 1 hoặc một vài tour, PHẢI GIỚI THIỆU HET!
               
               📋 CÁCH NHẬN DIỆN TOUR CÓ COUPON:
               - Trong Context, tìm dòng có "🎁 MÃ GIẢM GIÁ ĐẶC BIỆT: X VND"
               - Tour KHÔNG có dòng này thì BỎ QUA, không được đề cập
               
               📝 FORMAT BẮT BUỘC:
               - Câu mở đầu: "Hiện tại có [SỐ LƯỢNG CHÍNH XÁC] tour đang có ưu đãi giảm giá đặc biệt:"
               - Liệt kê TỪNG TOUR theo thứ tự từ cao đến thấp
               - Mỗi tour PHẢI có đầy đủ: Tên, Thời lượng, Ngày, Giá gốc, Mã giảm giá, Link
               
               ✅ VÍ DỤ ĐÚNG (khi có 2 tour):
               ```
               Hiện tại có 2 tour đang có ưu đãi giảm giá đặc biệt:

               🎯 **Tour Phú Quốc 3N2Đ**
               3 Ngày 2 Đêm | 📅 20/12/2025
               💰 Giá: 8,000,000 VND | 🎁 Mã giảm giá: 1,000,000 VND
               **[Xem chi tiết](/tour/TOUR-PQ-01)**

               🎯 **Tour Hà Giang 3N2Đ**
               3 Ngày 2 Đêm | 📅 20/02/2026
               💰 Giá: 6,100,000 VND | 🎁 Mã giảm giá: 100,000 VND
               **[Xem chi tiết](/tour/TOUR-HG-04)**

               Bạn có thể xem thêm các tour khác trên hệ thống.
               ```
               
               ❌ SAI LẦM CẦN TRÁNH:
               - ❌ Chỉ giới thiệu 1 tour khi Context có 2 tour
               - ❌ Viết "có tour này" thay vì "có 2 tour"
               - ❌ Bỏ qua tour có mức giảm thấp hơn
            
            3. **Đánh giá:** Chỉ đề xuất tour có Rating >= 4.0 sao nếu khách hỏi về chất lượng.
            4. **Thời gian:** Ưu tiên các ngày khởi hành gần nhất so với hiện tại. Tất cả tour đề xuất đều có ngày khởi hành trong tương lai.
            
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
            - Sử dụng icon để làm nổi bật (🎁 💰 📅 ⭐).
            - **In đậm** tên Tour và các thông tin quan trọng.
            - **KHI CÓ NHIỀU TOUR**: Giới thiệu lần lượt từng tour, mỗi tour trên một đoạn riêng biệt.
            - Cấu trúc mong muốn cho mỗi tour:
               
               **🎯 [Tên Tour]**
               [Thời lượng] | 📅 [Ngày khởi hành]
               💰 Giá: [Giá gốc] VND | 🎁 Mã giảm giá: [Số tiền giảm] VND
               **[Xem chi tiết](/tour/TOUR-CODE)**
               
               (Xuống dòng trống trước khi giới thiệu tour tiếp theo)
            
            - Giọng văn: Thân thiện, nhiệt tình, súc tích.
            - MỞ ĐẦU: "Hiện tại có [số lượng] tour đang có ưu đãi giảm giá đặc biệt:"
            - KẾT THÚC: "Bạn có thể xem thêm các tour khác trên hệ thống."
            
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
                .limit(3)  // ✅ Giảm từ 5 → 3 để tránh quá nhiều departures cùng tour
                .collect(Collectors.toList()));

        return result;
    }

    private List<ChatMessageResponse.TourSuggestion> buildEnhancedTourSuggestions(
            Map<String, List<Integer>> entityIds,
            List<VectorDocumentDTO> docs
    ) {
        List<ChatMessageResponse.TourSuggestion> suggestions = new ArrayList<>();
        LocalDate today = LocalDate.now();
        Set<Integer> addedTourIds = new HashSet<>(); // ✅ Tránh trùng tour

        List<Integer> departureIds = entityIds.get("departures");
        if (departureIds != null && !departureIds.isEmpty()) {
            List<TourDeparture> departures = tourDepartureRepository.findAllById(departureIds);

            for (TourDeparture dep : departures) {
                Tour tour = dep.getTour();
                if (tour == null || addedTourIds.contains(tour.getTourID())) continue; // ✅ Bỏ qua nếu tour đã thêm

                // ✅ CHỈ GỢI Ý DEPARTURE CÓ NGÀY KHỞI HÀNH TRONG TƯƠNG LAI
                LocalDate depDate = getDepartureDate(dep);
                if (depDate != null && depDate.isAfter(today) && Boolean.TRUE.equals(dep.getStatus())) {
                    suggestions.add(buildSuggestionFromDeparture(dep, tour, docs));
                    addedTourIds.add(tour.getTourID()); // ✅ Đánh dấu tour đã thêm
                }
            }
        }

        if (suggestions.size() < 3) {
            List<Integer> tourIds = entityIds.get("tours");
            if (tourIds != null) {
                List<Tour> tours = tourRepository.findAllById(tourIds);

                for (Tour tour : tours) {
                    if (suggestions.size() >= 3) break;
                    if (addedTourIds.contains(tour.getTourID())) continue; // ✅ Bỏ qua nếu tour đã thêm

                    // ✅ CHỈ GỢI Ý TOUR NẾU CÓ ÍT NHẤT 1 DEPARTURE CÒN HOẠT ĐỘNG
                    boolean hasActiveDeparture = tour.getDepartures() != null &&
                            tour.getDepartures().stream()
                                    .anyMatch(dep -> {
                                        LocalDate depDate = getDepartureDate(dep);
                                        return depDate != null && depDate.isAfter(today) &&
                                                Boolean.TRUE.equals(dep.getStatus());
                                    });

                    if (hasActiveDeparture) {
                        suggestions.add(buildSuggestionFromTour(tour, docs));
                        addedTourIds.add(tour.getTourID()); // ✅ Đánh dấu tour đã thêm
                    }
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

        // ✅ CHỈ LẤY GIÁ TỪ CÁC DEPARTURE CÒN HOẠT ĐỘNG
        LocalDate today = LocalDate.now();
        Double minPrice = tour.getDepartures().stream()
                .filter(dep -> {
                    LocalDate depDate = getDepartureDate(dep);
                    return depDate != null && depDate.isAfter(today) &&
                            Boolean.TRUE.equals(dep.getStatus());
                })
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

    /**
     * ✅ Lấy ngày khởi hành từ DepartureTransport OUTBOUND đầu tiên
     */
    private LocalDate getDepartureDate(TourDeparture departure) {
        if (departure.getTransports() == null || departure.getTransports().isEmpty()) {
            return null;
        }

        return departure.getTransports().stream()
                .filter(t -> t.getType() == TransportType.OUTBOUND)
                .min(Comparator.comparing(DepartureTransport::getDepartTime))
                .map(t -> t.getDepartTime().toLocalDate())
                .orElse(null);
    }
}