package com.delivereats.payment.domain.model;

import java.util.UUID;

public class Payment {

    private final String id;
    private final String orderId;
    private final double amount;
    private PaymentStatus status;
    private final String transactionId;

    public Payment(String orderId, double amount) {
        this.id = UUID.randomUUID().toString();
        this.orderId = orderId;
        this.amount = amount;
        this.status = PaymentStatus.PENDING;
        this.transactionId = "TXN-" + UUID.randomUUID().toString().substring(0, 8);
    }

    public String getId() {
        return id;
    }

    public String getOrderId() {
        return orderId;
    }

    public double getAmount() {
        return amount;
    }

    public PaymentStatus getStatus() {
        return status;
    }

    public void setStatus(PaymentStatus status) {
        this.status = status;
    }

    public String getTransactionId() {
        return transactionId;
    }
}
