package com.delivereats.rider.application.dto;

public record RiderStatusResponse(
        String orderId,
        String riderName,
        String status
) {
}
