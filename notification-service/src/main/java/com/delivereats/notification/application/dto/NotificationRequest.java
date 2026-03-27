package com.delivereats.notification.application.dto;

public record NotificationRequest(String orderId, String type, String customerName, String message) {
}
