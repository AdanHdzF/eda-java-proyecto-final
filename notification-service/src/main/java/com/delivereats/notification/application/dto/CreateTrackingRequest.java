package com.delivereats.notification.application.dto;

public record CreateTrackingRequest(String orderId, String riderName, String estimatedArrival) {
}
