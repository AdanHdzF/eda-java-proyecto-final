package com.delivereats.order.domain.port.in;

import com.delivereats.order.application.dto.CreateOrderRequest;
import com.delivereats.order.application.dto.OrderResponse;

public interface CreateOrderUseCase {
    OrderResponse createOrder(CreateOrderRequest request);
}
