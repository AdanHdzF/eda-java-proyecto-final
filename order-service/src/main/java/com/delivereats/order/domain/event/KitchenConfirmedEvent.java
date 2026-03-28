package com.delivereats.order.domain.event;

import com.delivereats.shared.domain.event.DomainEvent;

public record KitchenConfirmedEvent(String orderId, int estimatedMinutes, double totalAmount) implements DomainEvent {
}
