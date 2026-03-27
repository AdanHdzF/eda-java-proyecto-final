package com.delivereats.payment.application.dto;

public record PaymentStatusResponse(String orderId, String paymentId, String status, double amount) {
}
