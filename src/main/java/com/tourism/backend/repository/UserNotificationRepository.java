package com.tourism.backend.repository;

import com.tourism.backend.entity.UserNotification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface UserNotificationRepository extends JpaRepository<UserNotification, Integer> {

    /**
     * Tìm UserNotification theo userId và notificationId
     * QUAN TRỌNG: Phải join với notification để tìm đúng
     */
    @Query("SELECT un FROM UserNotification un " +
            "WHERE un.user.userID = :userId " +
            "AND un.notification.notificationID = :notificationId")
    Optional<UserNotification> findByUserIdAndNotificationId(
            @Param("userId") Integer userId,
            @Param("notificationId") Integer notificationId
    );

    /**
     * Lấy tất cả notifications của user, sắp xếp theo thời gian
     */
    @Query("SELECT un FROM UserNotification un " +
            "JOIN FETCH un.notification n " +
            "WHERE un.user.userID = :userId " +
            "ORDER BY n.createdAt DESC")
    Page<UserNotification> findByUserId(
            @Param("userId") Integer userId,
            Pageable pageable
    );

    /**
     * Đếm số notification chưa đọc của user
     */
    @Query("SELECT COUNT(un) FROM UserNotification un " +
            "WHERE un.user.userID = :userId " +
            "AND un.isRead = false")
    Long countUnreadByUserId(@Param("userId") Integer userId);

    /**
     * Đánh dấu tất cả notification của user là đã đọc
     */
    @Modifying
    @Query("UPDATE UserNotification un " +
            "SET un.isRead = true, un.readAt = :readAt " +
            "WHERE un.user.userID = :userId " +
            "AND un.isRead = false")
    int markAllAsReadByUserId(
            @Param("userId") Integer userId,
            @Param("readAt") LocalDateTime readAt
    );

    /**
     * Kiểm tra xem user đã có UserNotification cho notification này chưa
     */
    @Query("SELECT CASE WHEN COUNT(un) > 0 THEN true ELSE false END " +
            "FROM UserNotification un " +
            "WHERE un.user.userID = :userId " +
            "AND un.notification.notificationID = :notificationId")
    boolean existsByUserIdAndNotificationId(
            @Param("userId") Integer userId,
            @Param("notificationId") Integer notificationId
    );

    /**
     * Xóa UserNotifications cũ hơn cutoffDate
     */
    @Modifying
    @Query("DELETE FROM UserNotification un " +
            "WHERE un.notification.createdAt < :cutoffDate")
    void deleteOldUserNotifications(@Param("cutoffDate") LocalDateTime cutoffDate);
}