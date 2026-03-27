package com.delivereats.kitchen.application.dto;

public record PaymentRequest(String orderId, double amount, String customerName) {
}
