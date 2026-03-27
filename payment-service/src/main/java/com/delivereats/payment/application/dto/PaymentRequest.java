package com.delivereats.payment.application.dto;

public record PaymentRequest(String orderId, double amount, String customerName) {
}
