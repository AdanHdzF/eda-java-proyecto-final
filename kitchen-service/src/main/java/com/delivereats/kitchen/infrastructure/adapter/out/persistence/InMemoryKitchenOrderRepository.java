package com.delivereats.kitchen.infrastructure.adapter.out.persistence;

import com.delivereats.kitchen.domain.model.KitchenOrder;
import com.delivereats.kitchen.domain.port.out.KitchenOrderRepositoryPort;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryKitchenOrderRepository implements KitchenOrderRepositoryPort {

    private final ConcurrentHashMap<String, KitchenOrder> orders = new ConcurrentHashMap<>();

    @Override
    public void save(KitchenOrder order) {
        orders.put(order.getOrderId(), order);
    }

    @Override
    public Optional<KitchenOrder> findByOrderId(String orderId) {
        return Optional.ofNullable(orders.get(orderId));
    }
}
