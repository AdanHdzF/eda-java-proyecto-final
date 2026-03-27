package com.delivereats.order.domain.model;

public record OrderItem(String productName, double price, int quantity) {
}
