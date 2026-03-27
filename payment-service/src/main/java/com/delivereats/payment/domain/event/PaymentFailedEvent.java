package com.delivereats.payment.domain.event;

import com.delivereats.shared.domain.event.DomainEvent;

public record PaymentFailedEvent(String orderId, String reason) implements DomainEvent {
}
