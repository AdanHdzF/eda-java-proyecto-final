package com.delivereats.order.domain.event;

import com.delivereats.shared.domain.event.DomainEvent;

public record OrderCanceledEvent(String orderId, String reason) implements DomainEvent {
}
