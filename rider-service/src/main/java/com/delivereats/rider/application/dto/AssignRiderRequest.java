package com.delivereats.rider.application.dto;

public record AssignRiderRequest(
        String orderId,
        String restaurantAddress
) {
}
