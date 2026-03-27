package com.delivereats.payment.application.dto;

public record RefundResponse(String paymentId, String orderId, double amount, String status) {
}
