package com.tourism.backend.service;

import com.tourism.backend.dto.request.LoginRequest;
import com.tourism.backend.dto.request.RefreshTokenRequest;
import com.tourism.backend.dto.response.LoginResponse;
import com.tourism.backend.dto.response.TokenResponse;
import com.tourism.backend.entity.RefreshToken;
import com.tourism.backend.entity.User;
import jakarta.servlet.http.HttpServletRequest;

public interface AuthService {
    LoginResponse login(LoginRequest request, HttpServletRequest httpServletRequest);
    TokenResponse refreshToken(RefreshTokenRequest request);
    void logout(String refreshToken);
    void logoutAll(Integer userId);
    RefreshToken createRefreshToken(User user, String token, HttpServletRequest request);
    void cleanExpiredTokens();
}
