package com.delivereats.notification.infrastructure.adapter.out.persistence;

import com.delivereats.notification.domain.model.Notification;
import com.delivereats.notification.domain.port.out.NotificationRepositoryPort;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryNotificationRepository implements NotificationRepositoryPort {

    private final ConcurrentHashMap<String, List<Notification>> store = new ConcurrentHashMap<>();

    @Override
    public void save(Notification notification) {
        store.computeIfAbsent(notification.getOrderId(), k -> new ArrayList<>()).add(notification);
    }

    @Override
    public List<Notification> findByOrderId(String orderId) {
        return store.getOrDefault(orderId, List.of());
    }
}
