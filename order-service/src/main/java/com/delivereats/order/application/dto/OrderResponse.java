package com.delivereats.order.application.dto;

public record OrderResponse(String orderId, String status, double totalAmount, String message) {
}
