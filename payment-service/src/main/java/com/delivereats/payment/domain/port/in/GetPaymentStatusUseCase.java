package com.delivereats.payment.domain.port.in;

import com.delivereats.payment.application.dto.PaymentStatusResponse;

public interface GetPaymentStatusUseCase {
    PaymentStatusResponse getPaymentStatus(String orderId);
}
