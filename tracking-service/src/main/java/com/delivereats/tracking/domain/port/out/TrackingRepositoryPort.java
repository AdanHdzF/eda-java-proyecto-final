package com.delivereats.tracking.domain.port.out;

import com.delivereats.tracking.domain.model.TrackingEntry;

import java.util.Optional;

public interface TrackingRepositoryPort {
    void save(TrackingEntry entry);
    Optional<TrackingEntry> findByOrderId(String orderId);
}
