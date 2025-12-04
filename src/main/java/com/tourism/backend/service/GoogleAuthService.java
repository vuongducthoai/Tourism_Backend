package com.tourism.backend.service;

import com.tourism.backend.dto.request.GoogleLoginRequest;
import com.tourism.backend.dto.response.GoogleUserInfo;
import com.tourism.backend.dto.response.LoginResponse;
import com.tourism.backend.entity.RefreshToken;
import com.tourism.backend.entity.User;
import jakarta.servlet.http.HttpServletRequest;

public interface GoogleAuthService {
     LoginResponse loginWithGoogle(GoogleLoginRequest request, HttpServletRequest httpRequest);
     GoogleUserInfo verifyGoogleToken(String idTokenString);
     User findOrCreateUser(GoogleUserInfo googleUserInfo);
    RefreshToken createRefreshToken(User user, String token, HttpServletRequest request);
}
