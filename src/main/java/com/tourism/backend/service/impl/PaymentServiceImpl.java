package com.tourism.backend.service.impl;

import com.google.gson.Gson;
import com.tourism.backend.config.PayOSConfig;
import com.tourism.backend.config.VNPayConfig;
import com.tourism.backend.dto.payos.*;
import com.tourism.backend.dto.request.PaymentRequestDTO;
import com.tourism.backend.dto.response.PaymentResponseDTO;
import com.tourism.backend.entity.Booking;
import com.tourism.backend.entity.Payment;
import com.tourism.backend.enums.BookingStatus;
import com.tourism.backend.enums.PaymentMethod;
import com.tourism.backend.enums.PaymentStatus;
import com.tourism.backend.repository.BookingRepository;
import com.tourism.backend.repository.PaymentRepository;
import com.tourism.backend.service.PaymentService;
import com.tourism.backend.util.BankUntil;
import com.tourism.backend.util.VNPayUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {
    private final Gson gson = new Gson();
    private final VNPayConfig vnPayConfig;
    private final PayOSConfig payOSConfig;
    private final PayOSService payOSService;
    private final BookingRepository bookingRepository;
    private final PaymentRepository paymentRepository;

    @Override
    public PaymentResponseDTO createVNPayPayment(PaymentRequestDTO request, HttpServletRequest httpRequest) {
        Booking booking = bookingRepository.findByBookingCode(request.getBookingCode())
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        if (!BookingStatus.PENDING_PAYMENT.equals(booking.getBookingStatus())) {
            throw new RuntimeException("Booking is not in pending payment status");
        }

        try {
            // ✅ Tạo transaction reference (phải unique)
            String vnp_TxnRef = booking.getBookingCode() + "_" + System.currentTimeMillis();

            // ✅ Amount phải nhân 100 (VNPay yêu cầu)
            long amount = request.getAmount() * 100;

            // ✅ IP Address - FIXED: Convert IPv6 to IPv4
            String vnp_IpAddr = VNPayUtil.getIpAddress(httpRequest);
            if (vnp_IpAddr.contains(":")) {
                vnp_IpAddr = "127.0.0.1"; // Convert IPv6 localhost to IPv4
            }

            // ✅ Tạo datetime
            Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
            SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
            String vnp_CreateDate = formatter.format(cld.getTime());

            // ✅ Expire sau 15 phút
            cld.add(Calendar.MINUTE, 15);
            String vnp_ExpireDate = formatter.format(cld.getTime());

            // ✅ Build parameters (sử dụng TreeMap để tự động sort)
            Map<String, String> vnp_Params = new TreeMap<>();
            vnp_Params.put("vnp_Version", "2.1.0");
            vnp_Params.put("vnp_Command", "pay");
            vnp_Params.put("vnp_TmnCode", vnPayConfig.getTmnCode());
            vnp_Params.put("vnp_Amount", String.valueOf(amount));
            vnp_Params.put("vnp_CurrCode", "VND");
            vnp_Params.put("vnp_TxnRef", vnp_TxnRef);
            vnp_Params.put("vnp_OrderInfo", request.getOrderInfo());
            vnp_Params.put("vnp_OrderType", "other");
            vnp_Params.put("vnp_Locale", "vn");
            vnp_Params.put("vnp_ReturnUrl", vnPayConfig.getReturnUrl());
            vnp_Params.put("vnp_IpAddr", vnp_IpAddr);
            vnp_Params.put("vnp_CreateDate", vnp_CreateDate);
            vnp_Params.put("vnp_ExpireDate", vnp_ExpireDate);

            // ✅ CRITICAL FIX: Build hash data WITHOUT encoding
            StringBuilder hashData = new StringBuilder();
            StringBuilder query = new StringBuilder();

            for (Map.Entry<String, String> entry : vnp_Params.entrySet()) {
                String fieldName = entry.getKey();
                String fieldValue = entry.getValue();

                if (fieldValue != null && fieldValue.length() > 0) {
                    // ⚠️ IMPORTANT: Hash data KHÔNG encode
                    if (hashData.length() > 0) {
                        hashData.append('&');
                    }
                    hashData.append(fieldName).append('=').append(fieldValue);

                    // ✅ Query string CÓ encode
                    if (query.length() > 0) {
                        query.append('&');
                    }
                    query.append(URLEncoder.encode(fieldName, StandardCharsets.UTF_8.toString()))
                            .append('=')
                            .append(URLEncoder.encode(fieldValue, StandardCharsets.UTF_8.toString()));
                }
            }

            String queryUrl = query.toString();

            // ✅ Tạo secure hash từ hash data (KHÔNG encode)
            String vnp_SecureHash = VNPayUtil.hmacSHA512(vnPayConfig.getHashSecret(), hashData.toString());
            queryUrl += "&vnp_SecureHash=" + vnp_SecureHash;

            String paymentUrl = vnPayConfig.getApiUrl() + "?" + queryUrl;

            // ✅ Log để debug
            log.info("=== VNPay Payment URL ===");
            log.info("TxnRef: {}", vnp_TxnRef);
            log.info("Amount: {}", amount);
            log.info("IP Address: {}", vnp_IpAddr);
            log.info("Hash Data (NOT encoded): {}", hashData.toString());
            log.info("Secure Hash: {}", vnp_SecureHash);
            log.info("Payment URL: {}", paymentUrl);
            log.info("========================");

            // Save payment record
            Payment payment = new Payment();
            payment.setBooking(booking);
            payment.setPaymentMethod(PaymentMethod.VNPAY);
            payment.setTransactionId(vnp_TxnRef);
            payment.setAmount(BigDecimal.valueOf(request.getAmount()));
            payment.setStatus(PaymentStatus.PENDING);
            payment.setPaymentDate(LocalDateTime.now());
            paymentRepository.save(payment);

            return PaymentResponseDTO.builder()
                    .code("00")
                    .message("Success")
                    .paymentUrl(paymentUrl)
                    .transactionId(vnp_TxnRef)
                    .build();

        } catch (Exception e) {
            log.error("❌ Error creating VNPay payment", e);
            throw new RuntimeException("Error creating VNPay payment: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public int handleVNPayCallback(Map<String, String> params) {
        log.info("=== Handling VNPay Callback ===");
        log.info("Params: {}", params);

        String vnp_SecureHash = params.get("vnp_SecureHash");
        Map<String, String> paramsToVerify = new HashMap<>(params);
        paramsToVerify.remove("vnp_SecureHashType");
        paramsToVerify.remove("vnp_SecureHash");

        // Verify signature
        String signValue = VNPayUtil.hashAllFields(paramsToVerify, vnPayConfig.getHashSecret());

        log.info("Expected Hash: {}", signValue);
        log.info("Received Hash: {}", vnp_SecureHash);

        if (!signValue.equals(vnp_SecureHash)) {
            log.error("❌ Invalid signature!");
            return -1;
        }

        String vnp_TxnRef = params.get("vnp_TxnRef");
        String vnp_ResponseCode = params.get("vnp_ResponseCode");
        String vnp_TransactionNo = params.get("vnp_TransactionNo");
        String vnp_BankCode = params.get("vnp_BankCode");

        // Find payment by transaction ID
        Payment payment = paymentRepository.findByTransactionId(vnp_TxnRef)
                .orElseThrow(() -> new RuntimeException("Payment not found for txnRef: " + vnp_TxnRef));

        Booking booking = payment.getBooking();

        if ("00".equals(vnp_ResponseCode)) {
            log.info("✅ Payment SUCCESS - Booking: {}, TxnRef: {}", booking.getBookingCode(), vnp_TxnRef);

            payment.setStatus(PaymentStatus.SUCCESS);
            payment.setBankCode(vnp_BankCode);
            payment.setBankTransactionNo(vnp_TransactionNo);
            payment.setPaymentDate(LocalDateTime.now());

            booking.setBookingStatus(BookingStatus.PAID);

            // Update available slots
            int currentSlots = booking.getTourDeparture().getAvailableSlots();
            int newSlots = currentSlots - booking.getTotalPassengers();
            booking.getTourDeparture().setAvailableSlots(newSlots);

            log.info("Updated slots: {} -> {} (reduced by {})",
                    currentSlots, newSlots, booking.getTotalPassengers());

            paymentRepository.save(payment);
            bookingRepository.save(booking);

            return 1;
        } else {
            log.warn("⚠️ Payment FAILED - Booking: {}, ResponseCode: {}",
                    booking.getBookingCode(), vnp_ResponseCode);

            payment.setStatus(PaymentStatus.FAILED);
            payment.setBankCode(vnp_BankCode);
            payment.setBankTransactionNo(vnp_TransactionNo);
            paymentRepository.save(payment);

            return 0;
        }
    }

    @Override
    public PaymentResponseDTO createPayOSPayment(PaymentRequestDTO request) {
        log.info("=== Creating PayOS payment ===");
        log.info("Booking Code: {}", request.getBookingCode());
        log.info("Amount: {}", request.getAmount());

        // Validate và lấy booking
        Booking booking = bookingRepository.findByBookingCode(request.getBookingCode())
                .orElseThrow(() -> new RuntimeException("Booking not found: " + request.getBookingCode()));

        if (!BookingStatus.PENDING_PAYMENT.equals(booking.getBookingStatus())) {
            throw new RuntimeException("Booking is not in pending payment status. Current status: "
                    + booking.getBookingStatus());
        }

        try {
            // 1. HARDCODE SỐ TIỀN TEST
            int amount = 4000;

            // 2. TẠO ORDER CODE NGẮN GỌN (Tránh quá dài gây lỗi ở một số bank)
            // Lấy 8 số cuối của timestamp để đảm bảo unique mà không quá dài
            String timeStr = String.valueOf(System.currentTimeMillis());
            long orderCode = Long.parseLong(timeStr.substring(timeStr.length() - 8));

            // 3. DESCRIPTION ĐƠN GIẢN (Không dấu, không ký tự đặc biệt)
            // Chỉ lấy chữ và số từ bookingCode
            String cleanBookingCode = booking.getBookingCode().replaceAll("[^a-zA-Z0-9]", "");
            String description = "Thanh toan " + cleanBookingCode;
            // Cắt ngắn nếu dài quá 25 ký tự
            if (description.length() > 25) description = description.substring(0, 25);

            // 4. XỬ LÝ NULL CHO BUYER INFO (Rất quan trọng khi test)
            String buyerName = booking.getContactFullName();
            String buyerEmail = booking.getContactEmail();
            String buyerPhone = booking.getContactPhone();

            // Nếu null thì gán giá trị mặc định để không bị lỗi 20
            if (buyerName == null || buyerName.isEmpty()) buyerName = "Khach hang Test";
            if (buyerEmail == null || buyerEmail.isEmpty()) buyerEmail = "test@gmail.com";
            if (buyerPhone == null || buyerPhone.isEmpty()) buyerPhone = "0900000000";

            // Validate lại format phone (PayOS có thể check)
            buyerPhone = buyerPhone.replaceAll("[^0-9]", "");
            if (buyerPhone.length() > 10) buyerPhone = buyerPhone.substring(0, 10);

            // 5. TẠO ITEMS (Tổng tiền item phải bằng amount = 2000)
            List<ItemData> items = new ArrayList<>();
            items.add(ItemData.builder()
                    .name("Tour Test " + cleanBookingCode)
                    .quantity(1)
                    .price(amount) // Giá item = 2000
                    .build());

            // 6. BUILD PAYMENT DATA
            PaymentData paymentData = PaymentData.builder()
                    .orderCode(orderCode)
                    .amount(amount) // Tổng tiền = 2000
                    .description(description)
                    .items(items)
                    // Đảm bảo URL không có khoảng trắng
                    .returnUrl(payOSConfig.getReturnUrl().trim())
                    .cancelUrl(payOSConfig.getCancelUrl().trim())
                    .buyerName(buyerName)
                    .buyerEmail(buyerEmail)
                    .buyerPhone(buyerPhone)
                    .expiredAt((long) (System.currentTimeMillis() / 1000) + 900L)
                    .build();

            log.info("Payment Data Test: {}", gson.toJson(paymentData));

            // Gọi SDK
            CheckoutResponseData checkoutResponse = payOSService.createPaymentLink(paymentData);
            Payment payment = paymentRepository.findByBooking(booking)
                    .orElse(new Payment());

            if(payment.getPaymentID() == null){
                payment.setBooking(booking);
            }
            log.info("PayOS payment created successfully");
            log.info("Checkout URL: {}", checkoutResponse.getCheckoutUrl());
            log.info("Order Code: {}", orderCode);
            log.info("QR Code: {}", checkoutResponse.getQrCode());


            // Lưu payment record
            payment.setPaymentMethod(PaymentMethod.PAYOS);
            payment.setTransactionId(String.valueOf(orderCode));
            payment.setAmount(BigDecimal.valueOf(request.getAmount()));
            payment.setStatus(PaymentStatus.PENDING);
            payment.setTimeLimit(LocalDateTime.now().plusHours(24));
            payment.setPaymentDate(LocalDateTime.now());
            payment.setBankCode(null);
            payment.setBankTransactionNo(null);
            payment.setAccountName(null);
            payment.setAccountNumber(null);
            booking.setBookingStatus(BookingStatus.PENDING_PAYMENT);
            bookingRepository.save(booking);
            paymentRepository.save(payment);

            log.info("Payment record saved with transaction ID: {}", orderCode);

            return PaymentResponseDTO.builder()
                    .code("00")
                    .message("Success")
                    .paymentUrl(checkoutResponse.getCheckoutUrl())
                    .transactionId(String.valueOf(orderCode))
                    .qrCode(checkoutResponse.getQrCode())
                    .build();

        } catch (Exception e) {
            log.error("Error creating PayOS payment", e);
            throw new RuntimeException("Error creating PayOS payment: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional
    public int handlePayOSWebhook(String webhookData) {
        log.info("=== Handling PayOS Webhook ===");
        log.info("Webhook Data: {}", webhookData);

        try {
            // Parse webhook data
            WebhookRequest webhookRequest = gson.fromJson(webhookData, WebhookRequest.class);
            WebhookData data = webhookRequest.getData();

            if (data == null) {
                log.error("Webhook data is null");
                return -1;
            }

            String orderCode = String.valueOf(data.getOrderCode());
            String code = webhookRequest.getCode();

            log.info("Order Code: {}", orderCode);
            log.info("Response Code: {}", code);

            // Find payment by transaction ID (orderCode)
            Payment payment = paymentRepository.findByTransactionId(orderCode)
                    .orElseThrow(() -> new RuntimeException("Payment not found for orderCode: " + orderCode));

            Booking booking = payment.getBooking();

            // "00" = success
            if ("00".equals(code)) {
                log.info("Payment SUCCESS - Booking: {}, OrderCode: {}",
                        booking.getBookingCode(), orderCode);

                payment.setStatus(PaymentStatus.SUCCESS);
                payment.setPaymentDate(LocalDateTime.now());

                // Tên tài khoản người chuyển
                if (data.getCounterAccountName() != null && !data.getCounterAccountName().isEmpty()) {
                    payment.setAccountName(data.getCounterAccountName());
                    log.info("Account Name: {}", data.getCounterAccountName());
                }

                // Số tài khoản người chuyển
                if (data.getCounterAccountNumber() != null && !data.getCounterAccountNumber().isEmpty()) {
                    payment.setAccountNumber(data.getCounterAccountNumber());
                    log.info("Account Number: {}", data.getCounterAccountNumber());
                }

                // Mã ngân hàng (VCB, TCB, MB...)
                if (data.getCounterAccountBankId() != null && !data.getCounterAccountBankId().isEmpty()) {
                    payment.setBankCode(data.getCounterAccountBankId());
                    log.info("Bank Code: {}", data.getCounterAccountBankId());
                }

                // Tên ngân hàng đầy đủ
                if (data.getCounterAccountBankName() != null && !data.getCounterAccountBankName().isEmpty()) {
                    payment.setBank(data.getCounterAccountBankName());
                    log.info("Bank Name: {}", data.getCounterAccountBankName());
                }

                // Mã giao dịch ngân hàng
                if (data.getReference() != null && !data.getReference().isEmpty()) {
                    payment.setBankTransactionNo(data.getReference());
                    log.info("Bank Transaction No: {}", data.getReference());
                }

                // Thời gian giao dịch thực tế từ ngân hàng
                if (data.getTransactionDateTime() != null && !data.getTransactionDateTime().isEmpty()) {
                    try {
                        // PayOS trả về ISO 8601 format: "2024-12-10T14:30:00"
                        LocalDateTime transactionTime = LocalDateTime.parse(
                                data.getTransactionDateTime().replace("Z", "")
                        );
                        payment.setTransactionDatetime(transactionTime);
                        log.info("Transaction DateTime: {}", transactionTime);
                    } catch (Exception e) {
                        log.warn("Cannot parse transaction datetime: {}", data.getTransactionDateTime(), e);
                        // Fallback: dùng thời gian hiện tại
                        payment.setTransactionDatetime(LocalDateTime.now());
                    }
                } else {
                    payment.setTransactionDatetime(LocalDateTime.now());
                }

                // Nội dung chuyển khoản
                String description = "Thanh toan tour qua PayOS";
                if (data.getDescription() != null && !data.getDescription().isEmpty()) {
                    description = data.getDescription();
                }
                if (data.getReference() != null) {
                    description += " - Ma GD: " + data.getReference();
                }
                payment.setPaymentDescription(description);
                log.info("Payment Description: {}", description);

                booking.setBookingStatus(BookingStatus.PENDING_CONFIRMATION);

                // Update available slots
                int currentSlots = booking.getTourDeparture().getAvailableSlots();
                int newSlots = currentSlots - booking.getTotalPassengers();
                booking.getTourDeparture().setAvailableSlots(newSlots);

                log.info("Updated slots: {} -> {} (reduced by {})",
                        currentSlots, newSlots, booking.getTotalPassengers());

                paymentRepository.save(payment);
                bookingRepository.save(booking);

                return 1;
            } else {
                log.warn("⚠️ Payment FAILED/CANCELLED - Booking: {}, Code: {}",
                        booking.getBookingCode(), code);

                payment.setStatus(PaymentStatus.FAILED);

                // Lưu thông tin lỗi
                String errorDescription = "Giao dich that bai";
                if ("01".equals(code)) {
                    errorDescription += " - Da huy boi nguoi dung";
                } else if ("02".equals(code)) {
                    errorDescription += " - Loi he thong";
                } else {
                    errorDescription += " - Ma loi: " + code;
                }
                payment.setPaymentDescription(errorDescription);

                paymentRepository.save(payment);

                return 0;
            }

        } catch (Exception e) {
            log.error("❌ Error processing webhook", e);
            return -1;
        }
    }

    @Override
    public PaymentResponseDTO getPaymentStatus(Long orderCode) {
        log.info("=== Checking payment status for orderCode: {} ===", orderCode);

        try {
            // Get payment status from PayOS
            PaymentStatusData paymentInfo = payOSService.getPaymentInfo(orderCode);

            log.info("Payment Status: {}", paymentInfo.getStatus());
            log.info("Amount: {}", paymentInfo.getAmount());

            return PaymentResponseDTO.builder()
                    .code("00")
                    .message("Success")
                    .transactionId(String.valueOf(orderCode))
                    .status(paymentInfo.getStatus())
                    .amount(BigDecimal.valueOf(paymentInfo.getAmount()))
                    .build();

        } catch (Exception e) {
            log.error("Error checking payment status", e);
            throw new RuntimeException("Error checking payment status: " + e.getMessage(), e);
        }
    }


    @Override
    @Transactional
    public PaymentResponseDTO getPaymentStatusAndUpdate(Long orderCode) {
        log.info("=== Checking and updating payment status for orderCode: {} ===", orderCode);

        try {
            // Tìm payment record trong DB
            Payment payment = paymentRepository.findByTransactionId(String.valueOf(orderCode))
                    .orElseThrow(() -> new RuntimeException("Payment not found for orderCode: " + orderCode));

            Booking booking = payment.getBooking();

            // Nếu đã SUCCESS rồi thì không cần check nữa
            if (PaymentStatus.SUCCESS.equals(payment.getStatus())) {
                log.info("Payment already SUCCESS");
                return PaymentResponseDTO.builder()
                        .code("00")
                        .message("Payment already completed")
                        .transactionId(String.valueOf(orderCode))
                        .status("SUCCESS")
                        .amount(payment.getAmount())
                        .build();
            }

            // Gọi PayOS API để lấy thông tin mới nhất
            log.info("Fetching payment info from PayOS API...");
            PaymentStatusData paymentInfo = payOSService.getPaymentInfo(orderCode);

            log.info("Payment Status from PayOS: {}", paymentInfo.getStatus());

            // Kiểm tra status
            if ("PAID".equalsIgnoreCase(paymentInfo.getStatus())) {
                log.info("Payment SUCCESS - Updating database...");

                // Update payment status
                payment.setStatus(PaymentStatus.SUCCESS);
                payment.setPaymentDate(LocalDateTime.now());

                // KIỂM TRA NULL TRƯỚC KHI SỬ DỤNG TRANSACTIONS
                if (paymentInfo.getTransactions() != null && !paymentInfo.getTransactions().isEmpty()) {
                    var transaction = paymentInfo.getTransactions().get(0);

                    // Tên tài khoản - kiểm tra null
                    if (transaction.getCounterAccountName() != null && !transaction.getCounterAccountName().isEmpty()) {
                        payment.setAccountName(transaction.getCounterAccountName());
                        log.info("Account Name: {}", transaction.getCounterAccountName());
                    }

                    // Số tài khoản - kiểm tra null
                    if (transaction.getCounterAccountNumber() != null && !transaction.getCounterAccountNumber().isEmpty()) {
                        payment.setAccountNumber(transaction.getCounterAccountNumber());
                        log.info("Account Number: {}", transaction.getCounterAccountNumber());
                    }

                    // Mã ngân hàng - kiểm tra null
                    String bankCode = null;
                    if (transaction.getCounterAccountBankId() != null && !transaction.getCounterAccountBankId().isEmpty()) {
                        bankCode = transaction.getCounterAccountBankId();
                        payment.setBankCode(bankCode);
                        log.info("Bank Code: {}", bankCode);
                    }

                    // Tên ngân hàng - kiểm tra null
                    String bankName = transaction.getCounterAccountBankName();
                    if (bankName != null && !bankName.isEmpty()) {
                        payment.setBank(bankName);
                        log.info("Bank Name: {}", transaction.getCounterAccountBankName());
                    } else if(bankCode != null){
                        payment.setBank(BankUntil.getBankNameFromCode(bankCode));
                        log.info("Bank Name was null, mapped from code: {}", payment.getBank());
                    }

                    // Mã giao dịch ngân hàng - kiểm tra null
                    if (transaction.getReference() != null && !transaction.getReference().isEmpty()) {
                        payment.setBankTransactionNo(transaction.getReference());
                        log.info("Bank Transaction No: {}", transaction.getReference());
                    }

                    // Thời gian giao dịch - kiểm tra null
                    if (transaction.getTransactionDateTime() != null && !transaction.getTransactionDateTime().isEmpty()) {
                        try {
                            LocalDateTime transactionTime = LocalDateTime.parse(
                                    transaction.getTransactionDateTime().replace("Z", "")
                            );
                            payment.setTransactionDatetime(transactionTime);
                            log.info("Transaction DateTime: {}", transactionTime);
                        } catch (Exception e) {
                            log.warn("Cannot parse transaction datetime: {}", transaction.getTransactionDateTime(), e);
                            payment.setTransactionDatetime(LocalDateTime.now());
                        }
                    } else {
                        payment.setTransactionDatetime(LocalDateTime.now());
                    }

                    // Description - kiểm tra null
                    String description = "Thanh toan tour qua PayOS";
                    if (transaction.getDescription() != null && !transaction.getDescription().isEmpty()) {
                        description = transaction.getDescription();
                    }
                    if (transaction.getReference() != null && !transaction.getReference().isEmpty()) {
                        description += " - Ma GD: " + transaction.getReference();
                    }
                    payment.setPaymentDescription(description);
                    log.info("Description: {}", description);

                } else {
                    // KHÔNG CÓ TRANSACTIONS - Dùng giá trị mặc định
                    log.warn("No transaction data available from PayOS");
                    payment.setTransactionDatetime(LocalDateTime.now());
                    payment.setPaymentDescription("Thanh toan tour qua PayOS - Ma don: " + orderCode);
                }

                // Update booking status
                booking.setBookingStatus(BookingStatus.PENDING_CONFIRMATION);

                // Update available slots
                int currentSlots = booking.getTourDeparture().getAvailableSlots();
                int newSlots = currentSlots - booking.getTotalPassengers();
                booking.getTourDeparture().setAvailableSlots(newSlots);

                log.info("Updated slots: {} -> {} (reduced by {})",
                        currentSlots, newSlots, booking.getTotalPassengers());

                // Save to database
                paymentRepository.save(payment);
                bookingRepository.save(booking);

                log.info("Payment and booking updated successfully");

                return PaymentResponseDTO.builder()
                        .code("00")
                        .message("Payment successful")
                        .transactionId(String.valueOf(orderCode))
                        .status("SUCCESS")
                        .amount(payment.getAmount())
                        .build();

            } else if ("CANCELLED".equalsIgnoreCase(paymentInfo.getStatus())) {
                log.warn("Payment CANCELLED");

                payment.setStatus(PaymentStatus.FAILED);
                payment.setPaymentDescription("Giao dich da bi huy");
                paymentRepository.save(payment);

                return PaymentResponseDTO.builder()
                        .code("99")
                        .message("Payment cancelled")
                        .transactionId(String.valueOf(orderCode))
                        .status("CANCELLED")
                        .amount(payment.getAmount())
                        .build();

            } else {
                log.info("Payment still PENDING");

                return PaymentResponseDTO.builder()
                        .code("01")
                        .message("Payment pending")
                        .transactionId(String.valueOf(orderCode))
                        .status("PENDING")
                        .amount(payment.getAmount())
                        .build();
            }

        } catch (Exception e) {
            log.error("Error checking payment status", e);
            throw new RuntimeException("Error checking payment status: " + e.getMessage(), e);
        }
    }
}