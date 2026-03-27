package com.delivereats.payment.domain.port.in;

import com.delivereats.payment.application.dto.PaymentRequest;
import com.delivereats.payment.application.dto.PaymentResponse;

public interface ProcessPaymentUseCase {
    PaymentResponse processPayment(PaymentRequest request);
}
