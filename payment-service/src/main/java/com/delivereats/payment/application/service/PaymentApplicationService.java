package com.delivereats.payment.application.service;

import com.delivereats.payment.application.dto.AssignRiderRequest;
import com.delivereats.payment.application.dto.AssignRiderResponse;
import com.delivereats.payment.application.dto.PaymentRequest;
import com.delivereats.payment.application.dto.PaymentResponse;
import com.delivereats.payment.application.dto.PaymentStatusResponse;
import com.delivereats.payment.application.dto.RefundResponse;
import com.delivereats.payment.domain.model.Payment;
import com.delivereats.payment.domain.model.PaymentStatus;
import com.delivereats.payment.domain.port.in.GetPaymentStatusUseCase;
import com.delivereats.payment.domain.port.in.ProcessPaymentUseCase;
import com.delivereats.payment.domain.port.in.RefundPaymentUseCase;
import com.delivereats.payment.domain.port.out.PaymentRepositoryPort;
import com.delivereats.payment.domain.port.out.RiderPort;

public class PaymentApplicationService implements ProcessPaymentUseCase, RefundPaymentUseCase, GetPaymentStatusUseCase {

    private final PaymentRepositoryPort paymentRepository;
    private final RiderPort riderPort;

    public PaymentApplicationService(PaymentRepositoryPort paymentRepository, RiderPort riderPort) {
        this.paymentRepository = paymentRepository;
        this.riderPort = riderPort;
    }

    @Override
    public PaymentResponse processPayment(PaymentRequest request) {
        System.out.println("[PaymentService] Processing payment for order: " + request.orderId());

        // Simulate payment gateway latency (blocking call)
        try {
            System.out.println("[PaymentService] Contacting payment gateway... (12s)");
            Thread.sleep(12000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Payment processing interrupted", e);
        }

        Payment payment = new Payment(request.orderId(), request.amount());
        payment.setStatus(PaymentStatus.COMPLETED);
        paymentRepository.save(payment);

        System.out.println("[PaymentService] Payment completed: " + payment.getTransactionId());

        // Call Rider Service synchronously
        System.out.println("[PaymentService] Requesting rider assignment for order: " + request.orderId());
        AssignRiderRequest riderRequest = new AssignRiderRequest(request.orderId(), "Restaurant Address TBD");
        AssignRiderResponse riderResponse = riderPort.requestRider(riderRequest);
        System.out.println("[PaymentService] Rider assigned: " + riderResponse.riderName()
                + " (ETA: " + riderResponse.estimatedMinutes() + " min)");

        return new PaymentResponse(payment.getId(), payment.getStatus().name(), payment.getTransactionId());
    }

    @Override
    public RefundResponse refundPayment(String orderId) {
        System.out.println("[PaymentService] Processing refund for order: " + orderId);

        Payment payment = paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new RuntimeException("Payment not found for order: " + orderId));

        payment.setStatus(PaymentStatus.REFUNDED);
        paymentRepository.save(payment);

        System.out.println("[PaymentService] Refund completed for order: " + orderId);
        return new RefundResponse(payment.getId(), orderId, payment.getAmount(), payment.getStatus().name());
    }

    @Override
    public PaymentStatusResponse getPaymentStatus(String orderId) {
        Payment payment = paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new RuntimeException("Payment not found for order: " + orderId));

        return new PaymentStatusResponse(orderId, payment.getId(), payment.getStatus().name(), payment.getAmount());
    }
}
