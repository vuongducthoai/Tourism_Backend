package com.tourism.backend.service.impl;

import com.tourism.backend.service.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailServiceImpl implements EmailService {
    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Value("${app.frontend.url}")
    private String frontendUrl;

    @Async
    public void sendVerificationEmail(String toEmail, String fullName, String token) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(toEmail);
            helper.setSubject("Xác thực tài khoản Future Travel");

            // Tạo verification link
            String verificationLink = frontendUrl + "/verify-email?token=" + token;

            // Tạo HTML content
            Context context = new Context();
            context.setVariable("fullName", fullName);
            context.setVariable("verificationLink", verificationLink);

            String htmlContent = createEmailTemplate(fullName, verificationLink);
            helper.setText(htmlContent, true);

            mailSender.send(message);
            log.info("Verification email sent to: {}", toEmail);

        } catch (MessagingException e) {
            log.error("Failed to send email to: {}", toEmail, e);
            throw new RuntimeException("Could not send verification email");
        }
    }

    public String createEmailTemplate(String fullName, String verificationLink) {
        return "<!DOCTYPE html>" +
                "<html>" +
                "<head>" +
                "  <meta charset='UTF-8'>" +
                "  <style>" +
                "    body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }" +
                "    .container { max-width: 600px; margin: 0 auto; padding: 20px; }" +
                "    .header { background: linear-gradient(135deg, #d97706, #ea580c); color: white; padding: 30px; text-align: center; border-radius: 10px 10px 0 0; }" +
                "    .content { background: #f9fafb; padding: 30px; border-radius: 0 0 10px 10px; }" +
                "    .button { display: inline-block; background: linear-gradient(135deg, #d97706, #ea580c); color: white; padding: 15px 30px; text-decoration: none; border-radius: 8px; margin: 20px 0; font-weight: bold; }" +
                "    .footer { text-align: center; margin-top: 30px; color: #666; font-size: 12px; }" +
                "  </style>" +
                "</head>" +
                "<body>" +
                "  <div class='container'>" +
                "    <div class='header'>" +
                "      <h1>🎉 Chào mừng đến Future Travel!</h1>" +
                "    </div>" +
                "    <div class='content'>" +
                "      <p>Xin chào <strong>" + fullName + "</strong>,</p>" +
                "      <p>Cảm ơn bạn đã đăng ký tài khoản tại <strong>Future Travel</strong>!</p>" +
                "      <p>Để hoàn tất quá trình đăng ký, vui lòng nhấn vào nút bên dưới để xác thực email:</p>" +
                "      <div style='text-align: center;'>" +
                "        <a href='" + verificationLink + "' class='button'>Xác thực tài khoản</a>" +
                "      </div>" +
                "      <p style='color: #666; font-size: 14px;'>Hoặc copy link sau vào trình duyệt:</p>" +
                "      <p style='background: #e5e7eb; padding: 10px; border-radius: 5px; word-break: break-all;'>" + verificationLink + "</p>" +
                "      <p style='color: #ef4444; font-weight: bold;'>⚠️ Link này sẽ hết hạn sau 5 phút.</p>" +
                "      <p>Nếu bạn không thực hiện đăng ký này, vui lòng bỏ qua email.</p>" +
                "    </div>" +
                "    <div class='footer'>" +
                "      <p>&copy; 2025 Future Travel. All rights reserved.</p>" +
                "      <p>Địa chỉ: 123 Đường ABC, Quận XYZ, TP. Hồ Chí Minh</p>" +
                "    </div>" +
                "  </div>" +
                "</body>" +
                "</html>";
    }

    @Async
    public void sendWelcomeEmail(String toEmail, String fullName) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(toEmail);
            helper.setSubject("Chào mừng bạn đến với VietravelPlus!");

            String htmlContent = "<!DOCTYPE html>" +
                    "<html><body>" +
                    "<h2>Chào " + fullName + "!</h2>" +
                    "<p>Tài khoản của bạn đã được kích hoạt thành công. 🎉</p>" +
                    "<p>Bạn có thể bắt đầu khám phá các tour du lịch tuyệt vời của chúng tôi.</p>" +
                    "<a href='" + frontendUrl + "/login' style='background: #d97706; color: white; padding: 10px 20px; text-decoration: none; border-radius: 5px;'>Đăng nhập ngay</a>" +
                    "</body></html>";

            helper.setText(htmlContent, true);
            mailSender.send(message);
            log.info("Welcome email sent to: {}", toEmail);

        } catch (MessagingException e) {
            log.error("Failed to send welcome email to: {}", toEmail, e);
        }
    }
}
