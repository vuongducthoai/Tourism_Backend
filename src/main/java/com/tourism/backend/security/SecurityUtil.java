package com.tourism.backend.security;

import com.tourism.backend.exception.UnauthorizedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class SecurityUtil {

    public static Integer getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new UnauthorizedException("User chưa đăng nhập");
        }

        Object details = authentication.getDetails();
        if (details instanceof UserPrincipal) {
            return ((UserPrincipal) details).getUserId();
        }

        throw new UnauthorizedException("Không thể lấy thông tin user");
    }

    public static String getCurrentUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new UnauthorizedException("User chưa đăng nhập");
        }

        return authentication.getName();
    }

    public static String getCurrentUserRole() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new UnauthorizedException("User chưa đăng nhập");
        }

        Object details = authentication.getDetails();
        if (details instanceof UserPrincipal) {
            return ((UserPrincipal) details).getRole();
        }

        return null;
    }

    public static boolean isCurrentUser(Integer userId) {
        try {
            return getCurrentUserId().equals(userId);
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean isAdmin() {
        try {
            return "ADMIN".equals(getCurrentUserRole());
        } catch (Exception e) {
            return false;
        }
    }
}