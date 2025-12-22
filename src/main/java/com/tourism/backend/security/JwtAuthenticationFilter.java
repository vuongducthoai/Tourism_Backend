package com.tourism.backend.security;

import com.tourism.backend.convert.UserConverter;
import com.tourism.backend.dto.response.UserResponseDTO;
import com.tourism.backend.dto.responseDTO.UserReaponseDTO;
import com.tourism.backend.repository.UserRepository;
import com.tourism.backend.service.impl.WebSocketService;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cglib.core.Local;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.ConcurrentReferenceHashMap;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final WebSocketService webSocketService;
    private final UserConverter userConverter;

    private final Map<String, LocalDateTime> lastSocketSentTime = new ConcurrentHashMap<>();
    private static final long SOCKET_THROTTLE_SECONDS = 30;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");
        System.out.println("🔐 Authorization Header: " + authHeader);
        try {
            String jwt = getJwtFromRequest(request);

            if (StringUtils.hasText(jwt) && jwtUtil.validateToken(jwt)) {
                String email = jwtUtil.extractEmail(jwt);
                Integer userId = jwtUtil.extractUserId(jwt);
                String role = jwtUtil.extractRole(jwt);

                // Tạo Authentication object
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        email,
                        null,
                        Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role))
                );

                // Set thêm thông tin userId vào details
                authentication.setDetails(new UserPrincipal(userId, email, role));

                SecurityContextHolder.getContext().setAuthentication(authentication);

                log.debug("Set authentication for user: {} with role: {}", email, role);
                updateUserActivityWithSocket(email);
            }
        } catch (ExpiredJwtException e) {
            log.error("JWT token expired: {}", e.getMessage());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"error\":\"Token đã hết hạn\"}");
            return;
        } catch (Exception e) {
            log.error("Could not set user authentication: {}", e.getMessage());
        }

        filterChain.doFilter(request, response);
    }

    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    private void updateUserActivityWithSocket(String email) {
        try {
            LocalDateTime now = LocalDateTime.now();
            userRepository.updateLastActiveAt(email, now);

            LocalDateTime lastSent = lastSocketSentTime.get(email);
            boolean shouldSentSocket = lastSent == null || lastSent.plusSeconds(SOCKET_THROTTLE_SECONDS).isBefore(now);
            if(shouldSentSocket){
                userRepository.findByEmail(email).ifPresent(user -> {
                    UserReaponseDTO userDTO = userConverter.convertToUserResponseDTO(user);
                    userDTO.setLastActiveAt(now);

                    String activityStatus = "Online";
                    userDTO.setActivityStatus(activityStatus);
                    webSocketService.notifyUserActivityUpdate(userDTO);
                });
            }
            log.debug("Updated lastActiveAt for user: {}", email);
        } catch (Exception e) {
            log.warn("Could not update lastActiveAt for user {}: {}", email, e.getMessage());
        }
    }
}