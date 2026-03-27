package com.delivereats.payment.infrastructure.adapter.out.persistence;

import com.delivereats.payment.domain.model.Payment;
import com.delivereats.payment.domain.model.PaymentStatus;
import com.delivereats.payment.domain.port.out.PaymentRepositoryPort;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryPaymentRepository implements PaymentRepositoryPort {

    private final Map<String, Payment> payments = new ConcurrentHashMap<>();

    @Override
    public void save(Payment payment) {
        payments.put(payment.getOrderId(), payment);
    }

    @Override
    public Optional<Payment> findByOrderId(String orderId) {
        return Optional.ofNullable(payments.get(orderId));
    }

    @Override
    public void updateStatus(String orderId, PaymentStatus status) {
        Payment payment = payments.get(orderId);
        if (payment != null) {
            payment.setStatus(status);
        }
    }
}
