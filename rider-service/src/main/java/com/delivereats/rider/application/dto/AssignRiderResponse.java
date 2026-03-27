package com.delivereats.rider.application.dto;

public record AssignRiderResponse(
        String riderId,
        String riderName,
        int estimatedMinutes
) {
}
