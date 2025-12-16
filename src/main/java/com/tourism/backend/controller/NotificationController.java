package com.tourism.backend.controller;

import com.tourism.backend.dto.response.NotificationResponse;
import com.tourism.backend.entity.User; // Import User Entity
import com.tourism.backend.repository.UserRepository; // Import UserRepository
import com.tourism.backend.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails; // Import UserDetails
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@Slf4j
public class NotificationController {

    private final NotificationService notificationService;
    private final UserRepository userRepository;

    @GetMapping
    public ResponseEntity<Page<NotificationResponse>> getUserNotifications(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Integer userId = getCurrentUserId();
        log.info("Getting notifications for user {}, page: {}, size: {}", userId, page, size);

        Pageable pageable = PageRequest.of(page, size);
        Page<NotificationResponse> notifications = notificationService.getUserNotifications(userId, pageable);

        return ResponseEntity.ok(notifications);
    }

    @GetMapping("/unread-count")
    public ResponseEntity<Long> getUnreadCount() {
        Integer userId = getCurrentUserId();
        // log.debug("Getting unread count for user {}", userId); // Giảm log spam nếu cần
        System.out.println("UserID" + userId);

        Long count = notificationService.countUnread(userId);
        System.out.println("UnreadCount" + count);
        return ResponseEntity.ok(count);
    }

    @PutMapping("/{notificationId}/read")
    public ResponseEntity<Void> markAsRead(@PathVariable Integer notificationId) {
        Integer userId = getCurrentUserId();
        notificationService.markAsRead(notificationId, userId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/read-all")
    public ResponseEntity<Void> markAllAsRead() {
        Integer userId = getCurrentUserId();
        notificationService.markAllAsRead(userId);
        return ResponseEntity.ok().build();
    }

    // --- HÀM ĐÃ SỬA ---
    private Integer getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            throw new RuntimeException("User not authenticated");
        }

        Object principal = authentication.getPrincipal();
        String username;

        // Trường hợp 1: Principal chính là Entity User (nếu bạn đã cấu hình Custom UserDetails)
        if (principal instanceof User) {
            return ((User) principal).getUserID();
        }

        // Trường hợp 2: Principal là UserDetails chuẩn của Spring
        if (principal instanceof UserDetails) {
            username = ((UserDetails) principal).getUsername();
        }
        // Trường hợp 3: Principal là String (thường gặp khi dùng JWT decoder đơn giản)
        else if (principal instanceof String) {
            username = (String) principal;
        } else {
            username = null;
        }

        if (username != null) {
            // Tìm User trong DB để lấy ID.
            // Lưu ý: Thay 'findByEmail' bằng 'findByUsername' nếu DB của bạn dùng username
            User user = userRepository.findByEmail(username)
                    .orElseThrow(() -> new RuntimeException("User not found with email: " + username.toString() ));
            return user.getUserID();
        }

        throw new RuntimeException("Unable to get user ID from authentication. Principal type: " + principal.getClass());
    }
}