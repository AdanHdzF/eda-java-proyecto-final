package com.delivereats.rider.domain.port.out;

import com.delivereats.rider.domain.model.Assignment;

import java.util.Optional;

public interface AssignmentRepositoryPort {
    void save(Assignment assignment);
    Optional<Assignment> findByOrderId(String orderId);
}
