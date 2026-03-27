package com.delivereats.tracking.domain.port.in;

import com.delivereats.tracking.application.dto.TrackingResponse;

public interface GetTrackingUseCase {
    TrackingResponse getTracking(String orderId);
}
