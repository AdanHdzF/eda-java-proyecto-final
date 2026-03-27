package com.delivereats.kitchen.application.dto;

import java.util.List;

public record KitchenConfirmationRequest(String orderId, List<KitchenItemDto> items, String restaurantName) {
}
