package com.delivereats.notification.domain.port.in;

import com.delivereats.notification.application.dto.NotificationRequest;
import com.delivereats.notification.application.dto.NotificationResponse;

public interface SendNotificationUseCase {
    NotificationResponse sendNotification(NotificationRequest request);
}
