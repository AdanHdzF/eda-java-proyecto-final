package com.delivereats.kitchen.domain.event;

import com.delivereats.shared.domain.event.DomainEvent;

public record KitchenConfirmedEvent(String orderId, int estimatedMinutes) implements DomainEvent {
}
