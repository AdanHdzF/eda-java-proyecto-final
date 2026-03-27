package com.delivereats.payment.application.dto;

public record PaymentResponse(String paymentId, String status, String transactionId) {
}
