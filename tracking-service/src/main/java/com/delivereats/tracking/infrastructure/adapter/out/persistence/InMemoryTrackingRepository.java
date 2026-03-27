package com.delivereats.tracking.infrastructure.adapter.out.persistence;

import com.delivereats.tracking.domain.model.TrackingEntry;
import com.delivereats.tracking.domain.port.out.TrackingRepositoryPort;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryTrackingRepository implements TrackingRepositoryPort {

    private final ConcurrentHashMap<String, TrackingEntry> store = new ConcurrentHashMap<>();

    @Override
    public void save(TrackingEntry entry) {
        store.put(entry.getOrderId(), entry);
    }

    @Override
    public Optional<TrackingEntry> findByOrderId(String orderId) {
        return Optional.ofNullable(store.get(orderId));
    }
}
