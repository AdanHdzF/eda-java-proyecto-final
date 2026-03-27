package com.delivereats.tracking.application.service;

import com.delivereats.tracking.application.dto.CreateTrackingRequest;
import com.delivereats.tracking.application.dto.TrackingResponse;
import com.delivereats.tracking.domain.model.TrackingEntry;
import com.delivereats.tracking.domain.model.TrackingStatus;
import com.delivereats.tracking.domain.port.in.CreateTrackingUseCase;
import com.delivereats.tracking.domain.port.in.GetTrackingUseCase;
import com.delivereats.tracking.domain.port.out.TrackingRepositoryPort;

import java.util.Optional;
import java.util.UUID;

public class TrackingApplicationService implements CreateTrackingUseCase, GetTrackingUseCase {

    private final TrackingRepositoryPort trackingRepository;

    public TrackingApplicationService(TrackingRepositoryPort trackingRepository) {
        this.trackingRepository = trackingRepository;
    }

    @Override
    public TrackingResponse createTracking(CreateTrackingRequest request) {
        System.out.println("[Tracking] Creating tracking for order: " + request.orderId());

        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Interrupted while creating tracking", e);
        }

        UUID id = UUID.randomUUID();
        String trackingNumber = "TRK-" + id.toString().substring(0, 8);

        TrackingEntry entry = new TrackingEntry(
                id,
                request.orderId(),
                TrackingStatus.CREATED,
                request.riderName(),
                request.estimatedArrival(),
                trackingNumber
        );

        trackingRepository.save(entry);

        System.out.println("[Tracking] Tracking created: " + trackingNumber + " for order " + request.orderId());

        return new TrackingResponse(trackingNumber, TrackingStatus.CREATED.name(), request.riderName(), request.estimatedArrival());
    }

    @Override
    public TrackingResponse getTracking(String orderId) {
        Optional<TrackingEntry> entry = trackingRepository.findByOrderId(orderId);

        if (entry.isPresent()) {
            TrackingEntry tracking = entry.get();
            return new TrackingResponse(
                    tracking.getTrackingNumber(),
                    tracking.getStatus().name(),
                    tracking.getRiderName(),
                    tracking.getEstimatedArrival()
            );
        }

        return new TrackingResponse(null, null, null, null);
    }
}
