package com.delivereats.order.domain.port.out;

import com.delivereats.order.domain.model.Order;
import com.delivereats.order.domain.model.OrderStatus;

import java.util.List;
import java.util.Optional;

public interface OrderRepositoryPort {
    void save(Order order);
    Optional<Order> findById(String id);
    void updateStatus(String id, OrderStatus status);
    List<Order> findAll();
}
