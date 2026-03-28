package com.delivereats.payment.application.dto;

public record OrderItemDto(String productName, double price, int quantity) {
}
