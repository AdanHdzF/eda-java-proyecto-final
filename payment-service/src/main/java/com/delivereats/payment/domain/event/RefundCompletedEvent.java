package com.delivereats.payment.domain.event;

import com.delivereats.shared.domain.event.DomainEvent;

public record RefundCompletedEvent(String orderId, String paymentId, double amount) implements DomainEvent {
}
