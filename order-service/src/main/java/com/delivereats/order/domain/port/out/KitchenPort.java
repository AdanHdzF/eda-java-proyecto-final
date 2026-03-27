package com.delivereats.order.domain.port.out;

import com.delivereats.order.application.dto.KitchenConfirmationRequest;
import com.delivereats.order.application.dto.KitchenConfirmationResponse;

public interface KitchenPort {
    KitchenConfirmationResponse requestConfirmation(KitchenConfirmationRequest request);
}
