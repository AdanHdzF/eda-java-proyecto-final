package com.delivereats.rider.domain.port.in;

import com.delivereats.rider.application.dto.RiderStatusResponse;

public interface GetRiderStatusUseCase {
    RiderStatusResponse getRiderStatus(String orderId);
}
