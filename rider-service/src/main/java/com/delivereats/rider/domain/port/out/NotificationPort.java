package com.delivereats.rider.domain.port.out;

import com.delivereats.rider.application.dto.NotificationRequest;
import com.delivereats.rider.application.dto.NotificationResponse;

public interface NotificationPort {
    NotificationResponse sendNotification(NotificationRequest request);
}
