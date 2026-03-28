package com.delivereats.payment.domain.event;

import com.delivereats.shared.domain.event.DomainEvent;

public record KitchenConfirmedEvent(
		String orderId,
		String customerId,
		String customerName,
		int estimatedMinutes,
		double totalAmount) implements DomainEvent {
}
