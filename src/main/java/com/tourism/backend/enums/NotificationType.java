package com.tourism.backend.enums;

public enum NotificationType {
    // ========== Social Notifications ==========
    POST_COMMENT,   // Ai đó bình luận
    FOLLOW,         // Ai đó follow
    MENTION,        // Ai đó mention (@)
    SYSTEM,
    // ========== Coupon Notifications ==========
    NEW_COUPON,
    COUPON_CREATED,     // Coupon mới được tạo (Admin → User)
    COUPON_ASSIGNED,    // Coupon được gán cho user cụ thể
    COUPON_CLAIMED,     // User claim coupon thành công
    COUPON_USED,        // Coupon được sử dụng
    COUPON_EXPIRED,     // Coupon sắp hết hạn / đã hết hạn
    COUPON_RUNNING_OUT, // Coupon sắp hết (còn ít slot)
    COUPON_UPDATED,

    // ========== Booking Notifications ==========
    BOOKING_CONFIRMED,  // Booking được xác nhận
    BOOKING_CANCELLED,  // Booking bị hủy
    BOOKING_REMINDER,   // Nhắc nhở booking sắp đến

    // ========== Payment Notifications ==========
    PAYMENT_SUCCESS,    // Thanh toán thành công
    PAYMENT_FAILED,     // Thanh toán thất bại
    REFUND_PROCESSED,   // Hoàn tiền được xử lý

    // ========== User Account Notifications ==========
    ACCOUNT_LOCKED,     // Tài khoản bị khóa
    ACCOUNT_UNLOCKED,   // Tài khoản được mở khóa
    PASSWORD_CHANGED,   // Mật khẩu được thay đổi

    // ========== System Notifications ==========
    MAINTENANCE,        // Bảo trì hệ thống
    PROMOTION,          // Khuyến mãi đặc biệt
    ANNOUNCEMENT,        // Thông báo quan trọng

    //========== Comment Notification =============
    NEW_COMMENT,
    COMMENT_REPLY,
    COMMENT_LIKE,
    POST_LIKE
}
