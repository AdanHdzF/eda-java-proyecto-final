package com.delivereats.notification.domain.event;

import com.delivereats.shared.domain.event.DomainEvent;

public record RiderAssignedEvent(
		String orderId,
		String riderId,
		String riderName,
		int estimatedMinutes) implements DomainEvent {
}
