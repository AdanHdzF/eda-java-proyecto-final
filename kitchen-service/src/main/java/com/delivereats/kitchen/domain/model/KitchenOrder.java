package com.delivereats.kitchen.domain.model;

import java.util.List;
import java.util.UUID;

public class KitchenOrder {

    private final String id;
    private final String orderId;
    private final List<String> items;
    private KitchenStatus status;
    private final int estimatedMinutes;

    public KitchenOrder(String orderId, List<String> items, KitchenStatus status, int estimatedMinutes) {
        this.id = UUID.randomUUID().toString();
        this.orderId = orderId;
        this.items = items;
        this.status = status;
        this.estimatedMinutes = estimatedMinutes;
    }

    public String getId() {
        return id;
    }

    public String getOrderId() {
        return orderId;
    }

    public List<String> getItems() {
        return items;
    }

    public KitchenStatus getStatus() {
        return status;
    }

    public void setStatus(KitchenStatus status) {
        this.status = status;
    }

    public int getEstimatedMinutes() {
        return estimatedMinutes;
    }
}
