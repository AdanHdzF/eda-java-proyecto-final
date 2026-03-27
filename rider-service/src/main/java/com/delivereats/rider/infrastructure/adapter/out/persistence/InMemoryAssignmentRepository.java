package com.delivereats.rider.infrastructure.adapter.out.persistence;

import com.delivereats.rider.domain.model.Assignment;
import com.delivereats.rider.domain.port.out.AssignmentRepositoryPort;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryAssignmentRepository implements AssignmentRepositoryPort {

    private final ConcurrentHashMap<String, Assignment> assignments = new ConcurrentHashMap<>();

    @Override
    public void save(Assignment assignment) {
        assignments.put(assignment.getOrderId(), assignment);
    }

    @Override
    public Optional<Assignment> findByOrderId(String orderId) {
        return Optional.ofNullable(assignments.get(orderId));
    }
}
