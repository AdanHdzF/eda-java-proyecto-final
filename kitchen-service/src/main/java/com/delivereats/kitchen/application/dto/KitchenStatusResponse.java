package com.delivereats.kitchen.application.dto;

public record KitchenStatusResponse(String orderId, String status, int estimatedMinutes) {
}
