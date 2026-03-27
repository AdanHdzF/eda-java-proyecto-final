package com.delivereats.order.domain.model;

public enum OrderStatus {
    CREATED,
    KITCHEN_CONFIRMED,
    PAID,
    RIDER_ASSIGNED,
    NOTIFIED,
    TRACKING,
    CANCELLED
}
