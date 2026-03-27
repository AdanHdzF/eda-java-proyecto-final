package com.delivereats.tracking.domain.port.in;

import com.delivereats.tracking.application.dto.CreateTrackingRequest;
import com.delivereats.tracking.application.dto.TrackingResponse;

public interface CreateTrackingUseCase {
    TrackingResponse createTracking(CreateTrackingRequest request);
}
