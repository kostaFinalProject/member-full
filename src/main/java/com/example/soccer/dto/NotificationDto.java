package com.example.soccer.dto;

import com.example.soccer.domain.Notification;
import com.example.soccer.domain.NotificationType;
import lombok.Builder;

@Builder
public record NotificationDto(
        String message,
        NotificationType notificationType,
        String relatedUri,
        boolean isRead
) {

    public static NotificationDto fromEntity(Notification notification) {
        return NotificationDto.builder()
                .message(notification.getMessage())
                .notificationType(notification.getNotificationType())
                .relatedUri(notification.getRelatedUri())
                .isRead(notification.isRead())
                .build();
    }
}
