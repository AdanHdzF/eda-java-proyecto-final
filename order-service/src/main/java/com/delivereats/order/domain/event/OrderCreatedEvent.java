package com.delivereats.order.domain.event;

import com.delivereats.order.application.dto.OrderItemDto;
import com.delivereats.shared.domain.event.DomainEvent;

import java.util.List;

public record OrderCreatedEvent(
        String orderId,
        String customerId,
        String customerName,
        String restaurantName,
        List<OrderItemDto> items,
        double totalAmount
) implements DomainEvent {
}
