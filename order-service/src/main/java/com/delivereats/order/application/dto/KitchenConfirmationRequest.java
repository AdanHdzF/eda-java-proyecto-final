package com.delivereats.order.application.dto;

import java.util.List;

public record KitchenConfirmationRequest(String orderId, List<OrderItemDto> items, String restaurantName) {
}
