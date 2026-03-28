package com.delivereats.payment.infrastructure.adapter.in.event;

import com.delivereats.payment.application.dto.PaymentRequest;
import com.delivereats.payment.application.service.PaymentApplicationService;
import com.delivereats.payment.domain.event.OrderCreatedEvent;
import com.delivereats.shared.infrastructure.messaging.EventBus;

public class PaymentSubscriber {
	private final PaymentApplicationService paymentApplicationService;

	public PaymentSubscriber(EventBus eventBus, PaymentApplicationService paymentApplicationService) {
		this.paymentApplicationService = paymentApplicationService;

		eventBus.subscribe("orders.created",
				OrderCreatedEvent.class, this::onOrderCreated, "payment-service");
	}

	private void onOrderCreated(OrderCreatedEvent event) {
		System.out.println(
				"************************* [Payment] ************************* Received [orders.created] event: "
						+ event);

		paymentApplicationService.processPayment(
				new PaymentRequest(event.orderId(), event.totalAmount(), event.customerName()));
	}

}
