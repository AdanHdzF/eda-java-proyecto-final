package com.delivereats.rider.domain.port.in;

import com.delivereats.rider.application.dto.AssignRiderRequest;
import com.delivereats.rider.application.dto.AssignRiderResponse;

public interface AssignRiderUseCase {
    AssignRiderResponse assignRider(AssignRiderRequest request);
}
