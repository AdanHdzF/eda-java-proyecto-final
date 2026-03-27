package com.delivereats.notification.domain.port.out;

import com.delivereats.notification.domain.model.Notification;

import java.util.List;

public interface NotificationRepositoryPort {
    void save(Notification notification);
    List<Notification> findByOrderId(String orderId);
}
