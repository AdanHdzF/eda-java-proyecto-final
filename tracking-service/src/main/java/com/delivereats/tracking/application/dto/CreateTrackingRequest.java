package com.delivereats.tracking.application.dto;

public record CreateTrackingRequest(String orderId, String riderName, String estimatedArrival) {
}
