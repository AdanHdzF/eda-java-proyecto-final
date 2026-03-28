package com.delivereats.payment.domain.event;

import java.util.List;

import com.delivereats.payment.application.dto.OrderItemDto;
import com.delivereats.shared.domain.event.DomainEvent;

public record OrderCreatedEvent(
		String orderId,
		String customerId,
		String customerName,
		String restaurantName,
		List<OrderItemDto> items,
		double totalAmount) implements DomainEvent {
}
