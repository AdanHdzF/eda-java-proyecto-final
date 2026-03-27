package com.delivereats.kitchen.domain.port.out;

import com.delivereats.kitchen.domain.model.KitchenOrder;

import java.util.Optional;

public interface KitchenOrderRepositoryPort {
    void save(KitchenOrder order);
    Optional<KitchenOrder> findByOrderId(String orderId);
}
