package com.delivereats.notification.domain.event;

import com.delivereats.shared.domain.event.DomainEvent;

public record NotificationSentEvent(String orderId, String type, String recipient) implements DomainEvent {
}
