package com.delivereats.payment.infrastructure.adapter.in.event;

import com.delivereats.payment.application.dto.PaymentRequest;
import com.delivereats.payment.application.service.PaymentApplicationService;
import com.delivereats.payment.domain.event.KitchenConfirmedEvent;
import com.delivereats.shared.infrastructure.messaging.EventBus;

public class PaymentSubscriber {
	private final PaymentApplicationService paymentApplicationService;

	public PaymentSubscriber(EventBus eventBus, PaymentApplicationService paymentApplicationService) {
		this.paymentApplicationService = paymentApplicationService;

		eventBus.subscribe("kitchen.confirmed",
				KitchenConfirmedEvent.class, this::onKitchenConfirmed, "payment-service");
	}

	private void onKitchenConfirmed(KitchenConfirmedEvent event) {
		System.out.println(
				"************************* [Payment] ************************* Received [kitchen.confirmed] event: "
						+ event);

		paymentApplicationService.processPayment(
				new PaymentRequest(event.orderId(), event.totalAmount(), event.customerName()));
	}

}
