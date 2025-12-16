package com.tourism.backend.service;

import com.tourism.backend.dto.response.NotificationResponse;
import com.tourism.backend.entity.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface NotificationService {
    NotificationResponse createNotification(Integer userId, String type, String title,
                                            String message, Object metadata);

    NotificationResponse createBroadcastNotification(String type, String title,
                                                     String message, Object metadata);

    Page<NotificationResponse> getUserNotifications(Integer userId, Pageable pageable);

    void markAsRead(Integer notificationId, Integer userId);

    void markAllAsRead(Integer userId);

    Long countUnread(Integer userId);

    void cleanupOldNotifications();
    void distributeBroadcastToUsers(Notification notification);
}
