package com.tourism.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 1. Tắt CSRF (Quan trọng khi test bằng Postman)
                .csrf(AbstractHttpConfigurer::disable)

                // 2. Cấu hình quyền truy cập
                .authorizeHttpRequests(auth -> auth
                        // Cho phép truy cập tự do vào API tạo tour (để test)
                        .requestMatchers("/api/tours/**").permitAll()
                        .requestMatchers("/api/locations/**").permitAll()

                        // Các API khác thì phải đăng nhập (nếu sau này làm login)
                        .anyRequest().authenticated()
                );

        return http.build();
    }
}