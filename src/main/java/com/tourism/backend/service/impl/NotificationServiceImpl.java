package com.tourism.backend.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tourism.backend.dto.response.NotificationResponse;
import com.tourism.backend.entity.Notification;
import com.tourism.backend.entity.User;
import com.tourism.backend.entity.UserNotification;
import com.tourism.backend.exception.ResourceNotFoundException;
import com.tourism.backend.repository.NotificationRepository;
import com.tourism.backend.repository.UserNotificationRepository;
import com.tourism.backend.repository.UserRepository;
import com.tourism.backend.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserNotificationRepository userNotificationRepository;
    private final UserRepository userRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final ObjectMapper objectMapper;
    private final NotificationSyncService notificationSyncService;

    // [FIX 1] Inject chính nó (Self-injection) để kích hoạt @Async và @Transactional
    @Autowired
    @Lazy // Bắt buộc phải có @Lazy để tránh lỗi Circular Dependency (Vòng lặp)
    private NotificationService self;

    @Override
    @Transactional
    public NotificationResponse createNotification(Integer userId, String type, String title,
                                                   String message, Object metadata) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Notification notification = Notification.builder()
                .user(user)
                .type(type)
                .title(title)
                .message(message)
                .metadata(objectMapper.valueToTree(metadata))
                .build();

        notification = notificationRepository.save(notification);

        UserNotification userNotification = UserNotification.builder()
                .user(user)
                .notification(notification)
                .isRead(false)
                .build();

        userNotification = userNotificationRepository.save(userNotification);
        NotificationResponse response = mapToResponse(userNotification);

        messagingTemplate.convertAndSend("/topic/user/" + userId + "/notifications", response);
        log.info("Created notification for user {}: {}", userId, type);

        return response;
    }

    @Override
    @Transactional
    public NotificationResponse createBroadcastNotification(String type, String title,
                                                            String message, Object metadata) {
        log.info("Starting createBroadcastNotification - type: {}", type);

        Notification notification = Notification.builder()
                .user(null)
                .type(type)
                .title(title)
                .message(message)
                .metadata(objectMapper.valueToTree(metadata))
                .build();

        // 1. Lưu Notification gốc
        Notification savedNotification = notificationRepository.save(notification);
        // Flush để đảm bảo ID đã được sinh ra và transaction này commit dữ liệu cơ bản
        notificationRepository.flush();

        // [FIX 2] Gọi hàm distribute thông qua biến 'self' thay vì gọi trực tiếp
        // Điều này giúp Spring tách transaction mới cho quá trình phân phối
        try {
            self.distributeBroadcastToUsers(savedNotification);
        } catch (Exception e) {
            // Log lỗi nhưng KHÔNG throw exception để tránh rollback transaction lưu Notification gốc
            log.error("Error triggering async distribution: {}", e.getMessage());
        }

        return NotificationResponse.builder()
                .notificationID(savedNotification.getNotificationID())
                .type(savedNotification.getType())
                .title(savedNotification.getTitle())
                .message(savedNotification.getMessage())
                .metadata(savedNotification.getMetadata())
                .isRead(false)
                .createdAt(savedNotification.getCreatedAt())
                .build();
    }

    @Override
    @Async // Chạy ở luồng riêng biệt
    @Transactional(propagation = Propagation.REQUIRES_NEW) // Tạo transaction hoàn toàn mới
    public void distributeBroadcastToUsers(Notification notification) {
        log.info("Starting Async Broadcast distribution for Notification ID: {}", notification.getNotificationID());

        try {
            List<User> allUsers = userRepository.findAll();
            List<UserNotification> userNotifications = new ArrayList<>();

            for (User user : allUsers) {
                UserNotification un = UserNotification.builder()
                        .user(user)
                        .notification(notification)
                        .isRead(false)
                        .build();
                userNotifications.add(un);
            }

            // Batch save
            List<UserNotification> savedList = userNotificationRepository.saveAll(userNotifications);
            log.info("Saved {} UserNotifications asynchronously", savedList.size());

            // Gửi WebSocket
            for (UserNotification un : savedList) {
                try {
                    NotificationResponse response = mapToResponse(un);
                    messagingTemplate.convertAndSend(
                            "/topic/user/" + un.getUser().getUserID() + "/notifications",
                            response
                    );
                } catch (Exception e) {
                    log.error("Error sending socket to user {}", un.getUser().getUserID());
                }
            }
        } catch (Exception e) {
            log.error("Critical error in broadcast distribution", e);
            // Vì dùng REQUIRES_NEW, nếu lỗi ở đây chỉ rollback phần UserNotification,
            // Notification gốc ở bảng cha vẫn được giữ lại.
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Page<NotificationResponse> getUserNotifications(Integer userId, Pageable pageable) {
        notificationSyncService.syncBroadcastNotificationsForUser(userId);
        Page<UserNotification> userNotifications = userNotificationRepository.findByUserId(userId, pageable);
        List<NotificationResponse> responses = userNotifications.getContent().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
        return new PageImpl<>(responses, pageable, userNotifications.getTotalElements());
    }

    @Override
    @Transactional
    public void markAsRead(Integer notificationId, Integer userId) {
        log.info("Marking notification {} as read for user {}", notificationId, userId);
        UserNotification userNotification = userNotificationRepository
                .findByUserIdAndNotificationId(userId, notificationId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Notification " + notificationId + " not found for user " + userId));

        if (!userNotification.getIsRead()) {
            userNotification.setIsRead(true);
            userNotification.setReadAt(LocalDateTime.now());
            userNotificationRepository.save(userNotification);
        }
    }

    @Override
    @Transactional
    public void markAllAsRead(Integer userId) {
        userNotificationRepository.markAllAsReadByUserId(userId, LocalDateTime.now());
    }

    @Override
    @Transactional(readOnly = true)
    public Long countUnread(Integer userId) {
        notificationSyncService.syncBroadcastNotificationsForUser(userId);
        return userNotificationRepository.countUnreadByUserId(userId);
    }

    @Scheduled(cron = "0 0 2 * * *")
    @Override
    @Transactional
    public void cleanupOldNotifications() {
        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(30);
        userNotificationRepository.deleteOldUserNotifications(cutoffDate);
        notificationRepository.deleteOldNotifications(cutoffDate);
        log.info("Cleaned up notifications older than {}", cutoffDate);
    }

    private NotificationResponse mapToResponse(UserNotification userNotification) {
        Notification notification = userNotification.getNotification();
        return NotificationResponse.builder()
                .notificationID(notification.getNotificationID())
                .type(notification.getType())
                .title(notification.getTitle())
                .message(notification.getMessage())
                .metadata(notification.getMetadata())
                .isRead(userNotification.getIsRead())
                .createdAt(notification.getCreatedAt())
                .readAt(userNotification.getReadAt())
                .build();
    }
}