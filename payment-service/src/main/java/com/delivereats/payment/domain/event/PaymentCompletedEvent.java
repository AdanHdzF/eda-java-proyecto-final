package com.delivereats.payment.domain.event;

import com.delivereats.shared.domain.event.DomainEvent;

public record PaymentCompletedEvent(String orderId, String paymentId, double amount, String transactionId) implements DomainEvent {
}
