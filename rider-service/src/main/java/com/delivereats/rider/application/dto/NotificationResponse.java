package com.delivereats.rider.application.dto;

public record NotificationResponse(
        String notificationId,
        boolean sent
) {
}
