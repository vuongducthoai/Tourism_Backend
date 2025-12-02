package com.tourism.backend.service.impl;

import com.tourism.backend.entity.Booking;
import com.tourism.backend.entity.RefundInformation;
import com.tourism.backend.service.MailService;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class MailServiceImpl implements MailService {
    private final JavaMailSender mailSender;
    private final String ADMIN_EMAIL = "22110431@student.hcmute.edu.vn";

    @Override
    public void sendRefundRequestNotification(Booking booking, RefundInformation refundInfo, BigDecimal totalRefundAmount) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(ADMIN_EMAIL);
        message.setSubject("YÊU CẦU HOÀN TIỀN MỚI: Booking Code " + booking.getBookingCode());

        // Định dạng tiền tệ
        NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        String formattedAmount = currencyFormatter.format(totalRefundAmount);
        String tourName = booking.getTourDeparture().getTour().getTourName();
        String tourCode = booking.getTourDeparture().getTour().getTourCode();

        String emailContent = String.format(
                "Xin chào,\n\n" +
                        "Hệ thống nhận được một yêu cầu hủy và hoàn tiền mới.\n\n" +
                        "--- THÔNG TIN BOOKING ---\n" +
                        "Mã Booking: %s\n" +
                        "Tên Tour: %s\n" +
                        "Mã Tour: %s\n" +
                        "Trạng thái mới: Chờ hoàn tiền\n\n" +
                        "--- LIÊN HỆ KHÁCH HÀNG ---\n" +
                        "Họ & Tên: %s\n" +
                        "Email: %s\n" +
                        "Điện thoại: %s\n" +
                        "Địa chỉ: %s\n\n" +
                        "--- THÔNG TIN HOÀN TIỀN ---\n" +
                        "Số tiền yêu cầu hoàn (Tổng tiền đã trả + Coin đã dùng): %s\n" +
                        "Tên Tài khoản: %s\n" +
                        "Số Tài khoản: %s\n" +
                        "Ngân hàng: %s\n\n" +
                        "Vui lòng xử lý yêu cầu này.",
                booking.getBookingCode(), tourName, tourCode,
                booking.getContactFullName(), booking.getContactEmail(), booking.getContactPhone(), booking.getContactAddress(),
                formattedAmount,
                refundInfo.getAccountName(), refundInfo.getAccountNumber(), refundInfo.getBank()
        );

        message.setText(emailContent);
        mailSender.send(message);
    }
}