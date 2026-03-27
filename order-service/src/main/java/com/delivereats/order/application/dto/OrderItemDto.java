package com.delivereats.order.application.dto;

public record OrderItemDto(String productName, double price, int quantity) {
}
