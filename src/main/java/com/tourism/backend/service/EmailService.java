package com.tourism.backend.service;

public interface EmailService {
     void sendVerificationEmail(String toEmail, String fullName, String token);
    void sendWelcomeEmail(String toEmail, String fullName);
    String createEmailTemplate(String fullName, String verificationLink);
}
