package com.delivereats.tracking.domain.event;

import com.delivereats.shared.domain.event.DomainEvent;

public record TrackingCreatedEvent(String orderId, String trackingNumber) implements DomainEvent {
}
