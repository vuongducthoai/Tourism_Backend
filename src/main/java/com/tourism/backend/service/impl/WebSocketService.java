package com.tourism.backend.service.impl;

import com.tourism.backend.dto.responseDTO.BookingResponseDTO;
import com.tourism.backend.dto.responseDTO.UserReaponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
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

    public void notifyUserUpdate(UserReaponseDTO user) {
        messagingTemplate.convertAndSend("/topic/admin/users", user);
    }

    public void notifyUserActivityUpdate(UserReaponseDTO userDTO){
        try {
            messagingTemplate.convertAndSend("/topic/admin/user-updates", userDTO);
            log.info("Send user status update for userId: {}", userDTO.getUserID());
        }catch (Exception e){
            log.error("Error sending user update: {}", e.getMessage());
        }
    }
}