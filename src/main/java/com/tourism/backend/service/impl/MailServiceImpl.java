package com.tourism.backend.service.impl;

import com.tourism.backend.entity.Booking;
import com.tourism.backend.entity.RefundInformation;
import com.tourism.backend.entity.User;
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
    @Override
    public void sendPaymentConfirmationEmail(Booking booking) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(booking.getContactEmail());
        message.setSubject("XÁC NHẬN THANH TOÁN: Booking Code " + booking.getBookingCode());

        NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        String formattedAmount = currencyFormatter.format(booking.getTotalPrice());
        String tourName = booking.getTourDeparture().getTour().getTourName();
        String tourCode = booking.getTourDeparture().getTour().getTourCode();

        String emailContent = String.format(
                "Kính gửi Quý khách %s,\n\n" +
                        "Cảm ơn Quý khách đã tin tưởng và đặt tour tại Future Travel!\n\n" +
                        "--- THÔNG TIN ĐẶT TOUR ---\n" +
                        "Mã Booking: %s\n" +
                        "Tên Tour: %s\n" +
                        "Mã Tour: %s\n" +
                        "Trạng thái: Đã thanh toán và xác nhận\n" +
                        "Tổng tiền: %s\n\n" +
                        "Tour của Quý khách đã được xác nhận thành công. " +
                        "Chúng tôi sẽ liên hệ với Quý khách trước ngày khởi hành.\n\n" +
                        "Nếu có bất kỳ thắc mắc nào, vui lòng liên hệ:\n" +
                        "Email: trananhthu270904@gmail.com\n" +
                        "Điện thoại: 0339263066\n\n" +
                        "Trân trọng,\nFuture Travel Team",
                booking.getContactFullName(), booking.getBookingCode(), tourName, tourCode, formattedAmount
        );

        message.setText(emailContent);
        mailSender.send(message);
    }

    @Override
    public void sendCancellationEmail(Booking booking) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(booking.getContactEmail());
        message.setSubject("THÔNG BÁO HỦY TOUR: Booking Code " + booking.getBookingCode());

        String tourName = booking.getTourDeparture().getTour().getTourName();
        String tourCode = booking.getTourDeparture().getTour().getTourCode();

        String emailContent = String.format(
                "Kính gửi Quý khách %s,\n\n" +
                        "Rất tiếc phải thông báo rằng tour của Quý khách đã bị hủy.\n\n" +
                        "--- THÔNG TIN BOOKING ---\n" +
                        "Mã Booking: %s\n" +
                        "Tên Tour: %s\n" +
                        "Mã Tour: %s\n" +
                        "Trạng thái: Đã hủy\n\n" +
                        "--- LÝ DO HỦY ---\n" +
                        "%s\n\n" +
                        "Chúng tôi xin lỗi vì sự bất tiện này. " +
                        "Nếu có bất kỳ thắc mắc nào, vui lòng liên hệ:\n" +
                        "Email: trananhthu270904@gmail.com\n" +
                        "Điện thoại: 0339263066\n\n" +
                        "Trân trọng,\nFuture Travel Team",
                booking.getContactFullName(), booking.getBookingCode(), tourName, tourCode,
                booking.getCancelReason() != null ? booking.getCancelReason() : "Không rõ lý do"
        );

        message.setText(emailContent);
        mailSender.send(message);
    }

    @Override
    public void sendCancellationWithRefundEmail(Booking booking, BigDecimal refundAmount) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(booking.getContactEmail());
        message.setSubject("THÔNG BÁO HỦY TOUR VÀ HOÀN TIỀN: Booking Code " + booking.getBookingCode());

        NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        String formattedRefund = currencyFormatter.format(refundAmount);
        String tourName = booking.getTourDeparture().getTour().getTourName();
        String tourCode = booking.getTourDeparture().getTour().getTourCode();

        // Lấy thông tin tài khoản hoàn tiền
        String refundAccountInfo = "Chưa cung cấp";
        if (booking.getRefundInformation() != null) {
            refundAccountInfo = String.format(
                    "Ngân hàng: %s\nSố tài khoản: %s\nTên tài khoản: %s",
                    booking.getRefundInformation().getBank(),
                    booking.getRefundInformation().getAccountNumber(),
                    booking.getRefundInformation().getAccountName()
            );
        } else if (booking.getPayment() != null) {
            refundAccountInfo = String.format(
                    "Ngân hàng: %s\nSố tài khoản: %s\nTên tài khoản: %s",
                    booking.getPayment().getBank(),
                    booking.getPayment().getAccountNumber(),
                    booking.getPayment().getAccountName()
            );
        }

        String emailContent = String.format(
                "Kính gửi Quý khách %s,\n\n" +
                        "Rất tiếc phải thông báo rằng tour của Quý khách đã bị hủy.\n\n" +
                        "--- THÔNG TIN BOOKING ---\n" +
                        "Mã Booking: %s\n" +
                        "Tên Tour: %s\n" +
                        "Mã Tour: %s\n" +
                        "Trạng thái: Đã hủy\n\n" +
                        "--- LÝ DO HỦY ---\n" +
                        "%s\n\n" +
                        "--- THÔNG TIN HOÀN TIỀN ---\n" +
                        "Số tiền hoàn: %s\n" +
                        "%s\n\n" +
                        "Chúng tôi đã hoàn trả số tiền đặt tour của Quý khách " +
                        "(bao gồm cả điểm tích lũy cá nhân nếu có sử dụng).\n\n" +
                        "Chúng tôi xin lỗi vì sự bất tiện này. " +
                        "Nếu có bất kỳ thắc mắc nào, vui lòng liên hệ:\n" +
                        "Email: trananhthu270904@gmail.com\n" +
                        "Điện thoại: 0339263066\n\n" +
                        "Trân trọng,\nFuture Travel Team",
                booking.getContactFullName(), booking.getBookingCode(), tourName, tourCode,
                booking.getCancelReason() != null ? booking.getCancelReason() : "Không rõ lý do",
                formattedRefund, refundAccountInfo
        );

        message.setText(emailContent);
        mailSender.send(message);
    }

    @Override
    public void sendAccountStatusEmail(User user, Boolean status, String reason) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(user.getEmail());

        String action = status ? "MỞ KHÓA" : "KHÓA";
        message.setSubject("THÔNG BÁO " + action + " TÀI KHOẢN - FUTURE TRAVEL");

        String content = String.format(
                "Xin chào %s,\n\n" +
                        "Tài khoản của bạn đã được %s.\n\n" +
                        "--- THÔNG TIN TÀI KHOẢN ---\n" +
                        "Họ tên: %s\n" +
                        "Email: %s\n" +
                        "Số điện thoại: %s\n" +
                        "Ngày sinh: %s\n\n" +
                        "--- LÝ DO ---\n%s\n\n" +
                        "Nếu có bất kỳ thắc mắc nào, vui lòng liên hệ:\n" +
                        "Email: trananhthu270904@gmail.com\n" +
                        "Điện thoại: 0339263066\n\n" +
                        "Trân trọng,\nFuture Travel Team",
                user.getFullName(),
                (status ? "mở khóa hoạt động trở lại" : "tạm khóa"),
                user.getFullName(), user.getEmail(), user.getPhone(), user.getDateOfBirth(),
                reason
        );

        message.setText(content);
        mailSender.send(message);
    }
}