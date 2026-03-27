package com.delivereats.tracking.application.dto;

public record TrackingResponse(String trackingNumber, String status, String riderName, String estimatedArrival) {
}
