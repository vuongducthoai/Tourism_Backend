package com.tourism.backend.service;

import com.tourism.backend.dto.request.PaymentRequestDTO;
import com.tourism.backend.dto.response.PaymentResponseDTO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Map;

public interface PaymentService {
    PaymentResponseDTO createVNPayPayment(PaymentRequestDTO request, HttpServletRequest httpRequest);
    int handleVNPayCallback(Map<String, String> params);
}