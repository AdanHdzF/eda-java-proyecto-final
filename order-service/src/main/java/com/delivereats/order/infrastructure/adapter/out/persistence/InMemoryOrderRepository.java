package com.delivereats.order.infrastructure.adapter.out.persistence;

import com.delivereats.order.domain.model.Order;
import com.delivereats.order.domain.model.OrderStatus;
import com.delivereats.order.domain.port.out.OrderRepositoryPort;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryOrderRepository implements OrderRepositoryPort {

    private final ConcurrentHashMap<String, Order> store = new ConcurrentHashMap<>();

    @Override
    public void save(Order order) {
        store.put(order.getId(), order);
    }

    @Override
    public Optional<Order> findById(String id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public void updateStatus(String id, OrderStatus status) {
        Order order = store.get(id);
        if (order != null) {
            order.updateStatus(status);
        }
    }

    @Override
    public List<Order> findAll() {
        return new ArrayList<>(store.values());
    }
}
