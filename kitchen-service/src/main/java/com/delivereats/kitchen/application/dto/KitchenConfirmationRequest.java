package com.delivereats.kitchen.application.dto;

import java.util.List;

public record KitchenConfirmationRequest(
		String orderId,
		String customerId,
		String customerName,
		List<KitchenItemDto> items,
		String restaurantName) {
}
