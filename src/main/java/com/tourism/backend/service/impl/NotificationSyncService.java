package com.tourism.backend.service.impl;

import com.tourism.backend.entity.Notification;
import com.tourism.backend.entity.User;
import com.tourism.backend.entity.UserNotification;
import com.tourism.backend.exception.ResourceNotFoundException;
import com.tourism.backend.repository.NotificationRepository;
import com.tourism.backend.repository.UserNotificationRepository;
import com.tourism.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationSyncService {

    private final NotificationRepository notificationRepository;
    private final UserNotificationRepository userNotificationRepository;
    private final UserRepository userRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void syncBroadcastNotificationsForUser(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        List<Notification> broadcastNotifications = notificationRepository.findAllBroadcastNotifications();

        List<UserNotification> newUserNotifications = new ArrayList<>();

        for (Notification notification : broadcastNotifications) {
            boolean exists = userNotificationRepository.existsByUserIdAndNotificationId(
                    userId, notification.getNotificationID()
            );

            if (!exists) {
                UserNotification userNotification = UserNotification.builder()
                        .user(user)
                        .notification(notification)
                        .isRead(false)
                        .build();

                newUserNotifications.add(userNotification);
            }
        }

        if (!newUserNotifications.isEmpty()) {
            userNotificationRepository.saveAll(newUserNotifications);
            log.debug("Synced {} broadcast notifications for user {}",
                    newUserNotifications.size(), userId);
        }
    }
}