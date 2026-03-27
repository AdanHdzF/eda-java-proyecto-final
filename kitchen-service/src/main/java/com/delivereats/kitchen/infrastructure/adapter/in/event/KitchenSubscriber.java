package com.delivereats.kitchen.infrastructure.adapter.in.event;

import java.util.List;

import com.delivereats.kitchen.application.dto.KitchenConfirmationRequest;
import com.delivereats.kitchen.domain.event.OrderCreatedEvent;
import com.delivereats.kitchen.domain.port.in.ConfirmOrderUseCase;
import com.delivereats.shared.infrastructure.messaging.EventBus;

public class KitchenSubscriber {
	private final ConfirmOrderUseCase confirmOrderUseCase;

	public KitchenSubscriber(EventBus eventBus, ConfirmOrderUseCase confirmOrderUseCase) {
		this.confirmOrderUseCase = confirmOrderUseCase;
		eventBus.subscribe("orders.created", OrderCreatedEvent.class, this::onOrderCreated, "kitchen-service");

	}

	private void onOrderCreated(OrderCreatedEvent event) {

		System.out.println(
				"************************* [Kitchen] ************************* Received order created event: "
						+ event);

		confirmOrderUseCase.confirmOrder(
				new KitchenConfirmationRequest(event.orderId(), (List) event.items(),
						event.restaurantName()));

	}

}
