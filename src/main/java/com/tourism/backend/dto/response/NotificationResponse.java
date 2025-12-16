package com.tourism.backend.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class NotificationResponse {
    private Integer notificationID;
    private String type;
    private String title;
    private String message;
    private Object metadata;
    private Boolean isRead;
    private LocalDateTime createdAt;
    private LocalDateTime readAt;
}
