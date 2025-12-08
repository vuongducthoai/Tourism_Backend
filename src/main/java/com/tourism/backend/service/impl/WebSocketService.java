package com.tourism.backend.service.impl;

import com.tourism.backend.dto.responseDTO.BookingResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WebSocketService {

    private final SimpMessagingTemplate messagingTemplate;

    /**
     * Gửi thông báo cập nhật booking cho tất cả admin
     */
    public void notifyAdminBookingUpdate(BookingResponseDTO booking) {
        messagingTemplate.convertAndSend("/topic/admin/bookings", booking);
    }

    /**
     * Gửi thông báo cập nhật booking cho user cụ thể
     */
    public void notifyUserBookingUpdate(Integer userId, BookingResponseDTO booking) {
        messagingTemplate.convertAndSend("/topic/user/" + userId + "/bookings", booking);
    }
}