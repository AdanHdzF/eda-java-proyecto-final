package com.delivereats.payment.domain.port.out;

import com.delivereats.payment.application.dto.AssignRiderRequest;
import com.delivereats.payment.application.dto.AssignRiderResponse;

public interface RiderPort {
    AssignRiderResponse requestRider(AssignRiderRequest request);
}
