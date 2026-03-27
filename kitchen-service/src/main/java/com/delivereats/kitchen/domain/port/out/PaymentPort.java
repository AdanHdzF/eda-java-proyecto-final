package com.delivereats.kitchen.domain.port.out;

import com.delivereats.kitchen.application.dto.PaymentRequest;
import com.delivereats.kitchen.application.dto.PaymentResponse;

public interface PaymentPort {
    PaymentResponse requestPayment(PaymentRequest request);
}
