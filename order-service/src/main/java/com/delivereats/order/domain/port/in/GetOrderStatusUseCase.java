package com.delivereats.order.domain.port.in;

import com.delivereats.order.application.dto.OrderStatusDto;

public interface GetOrderStatusUseCase {
    OrderStatusDto getOrderStatus(String orderId);
}
