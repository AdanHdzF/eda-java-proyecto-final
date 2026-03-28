package com.delivereats.kitchen.domain.event;

import java.util.List;

import com.delivereats.kitchen.application.dto.KitchenItemDto;
import com.delivereats.shared.domain.event.DomainEvent;

public record OrderCreatedEvent(
		String orderId,
		String customerId,
		String customerName,
		String restaurantName,
		List<KitchenItemDto> items,
		double totalAmount) implements DomainEvent {
}
