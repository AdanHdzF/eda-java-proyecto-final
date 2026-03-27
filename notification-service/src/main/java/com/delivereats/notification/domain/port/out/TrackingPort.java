package com.delivereats.notification.domain.port.out;

import com.delivereats.notification.application.dto.CreateTrackingRequest;
import com.delivereats.notification.application.dto.TrackingResponse;

public interface TrackingPort {
    TrackingResponse createTracking(CreateTrackingRequest request);
}
