package com.delivereats.payment.domain.port.in;

import com.delivereats.payment.application.dto.RefundResponse;

public interface RefundPaymentUseCase {
    RefundResponse refundPayment(String orderId);
}
