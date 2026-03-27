package com.delivereats.order.domain.port.in;

public interface CancelOrderUseCase {
    void cancelOrder(String orderId, String reason);
}
