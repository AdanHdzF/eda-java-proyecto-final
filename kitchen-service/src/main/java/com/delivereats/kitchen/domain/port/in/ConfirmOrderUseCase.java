package com.delivereats.kitchen.domain.port.in;

import com.delivereats.kitchen.application.dto.KitchenConfirmationRequest;
import com.delivereats.kitchen.application.dto.KitchenConfirmationResponse;

public interface ConfirmOrderUseCase {
    KitchenConfirmationResponse confirmOrder(KitchenConfirmationRequest request);
}
