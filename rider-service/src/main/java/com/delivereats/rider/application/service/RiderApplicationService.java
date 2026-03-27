package com.delivereats.rider.application.service;

import com.delivereats.rider.application.dto.AssignRiderRequest;
import com.delivereats.rider.application.dto.AssignRiderResponse;
import com.delivereats.rider.application.dto.NotificationRequest;
import com.delivereats.rider.application.dto.RiderStatusResponse;
import com.delivereats.rider.domain.model.Assignment;
import com.delivereats.rider.domain.model.Rider;
import com.delivereats.rider.domain.model.RiderStatus;
import com.delivereats.rider.domain.port.in.AssignRiderUseCase;
import com.delivereats.rider.domain.port.in.GetRiderStatusUseCase;
import com.delivereats.rider.domain.port.out.AssignmentRepositoryPort;
import com.delivereats.rider.domain.port.out.NotificationPort;
import com.delivereats.rider.domain.port.out.RiderRepositoryPort;

import java.util.UUID;

public class RiderApplicationService implements AssignRiderUseCase, GetRiderStatusUseCase {

    private final RiderRepositoryPort riderRepository;
    private final AssignmentRepositoryPort assignmentRepository;
    private final NotificationPort notificationPort;

    public RiderApplicationService(RiderRepositoryPort riderRepository,
                                   AssignmentRepositoryPort assignmentRepository,
                                   NotificationPort notificationPort) {
        this.riderRepository = riderRepository;
        this.assignmentRepository = assignmentRepository;
        this.notificationPort = notificationPort;
    }

    @Override
    public AssignRiderResponse assignRider(AssignRiderRequest request) {
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Rider assignment interrupted", e);
        }

        Rider rider = riderRepository.findAvailable()
                .orElseThrow(() -> new RuntimeException("No available riders"));

        Assignment assignment = new Assignment(
                UUID.randomUUID().toString(),
                request.orderId(),
                rider.getId(),
                rider.getName(),
                "ASSIGNED"
        );

        assignmentRepository.save(assignment);
        riderRepository.updateStatus(rider.getId(), RiderStatus.ASSIGNED);

        int estimatedMinutes = 30;

        notificationPort.sendNotification(new NotificationRequest(
                request.orderId(),
                "RIDER_ASSIGNED",
                rider.getName(),
                "Rider " + rider.getName() + " asignado a tu pedido"
        ));

        return new AssignRiderResponse(rider.getId(), rider.getName(), estimatedMinutes);
    }

    @Override
    public RiderStatusResponse getRiderStatus(String orderId) {
        Assignment assignment = assignmentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new RuntimeException("No assignment found for order: " + orderId));

        return new RiderStatusResponse(assignment.getOrderId(), assignment.getRiderName(), assignment.getStatus());
    }
}
