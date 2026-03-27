package com.delivereats.order.application.dto;

public record OrderStatusDto(
        String orderId,
        String customerName,
        String restaurantName,
        String status,
        double totalAmount,
        String kitchenStatus,
        String paymentStatus,
        String riderName,
        String trackingNumber
) {
}
