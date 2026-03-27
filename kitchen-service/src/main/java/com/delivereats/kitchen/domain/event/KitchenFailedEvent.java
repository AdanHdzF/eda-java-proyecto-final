package com.delivereats.kitchen.domain.event;

import com.delivereats.shared.domain.event.DomainEvent;

public record KitchenFailedEvent(String orderId, String reason) implements DomainEvent {
}
