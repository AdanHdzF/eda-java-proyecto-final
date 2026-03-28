package com.delivereats.order.domain.port.in;

import com.delivereats.order.application.dto.CreateOrderRequest;
import com.delivereats.order.application.dto.OrderResponse;
import com.delivereats.order.application.dto.OrderStatusDto;
import com.delivereats.order.domain.model.OrderStatus;

public interface CreateOrderUseCase {
	OrderResponse createOrder(CreateOrderRequest request);

	OrderStatusDto getOrderStatus(String orderId);

	OrderStatusDto updateOrderStatus(String orderId, OrderStatus newStatus);

	void cancelOrder(String orderId, String reason);
}
