package com.delivereats.order.application.dto;

import java.util.List;

public record CreateOrderRequest(String customerName, String restaurantName, List<OrderItemDto> items) {
}
