package com.delivereats.payment.domain.port.out;

import com.delivereats.payment.domain.model.Payment;
import com.delivereats.payment.domain.model.PaymentStatus;

import java.util.Optional;

public interface PaymentRepositoryPort {
    void save(Payment payment);
    Optional<Payment> findByOrderId(String orderId);
    void updateStatus(String orderId, PaymentStatus status);
}
