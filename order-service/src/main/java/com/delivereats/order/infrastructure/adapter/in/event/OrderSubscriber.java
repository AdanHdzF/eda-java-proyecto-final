package com.delivereats.order.infrastructure.adapter.in.event;

import com.delivereats.order.application.service.OrderApplicationService;
import com.delivereats.order.domain.event.KitchenConfirmedEvent;
import com.delivereats.order.domain.model.OrderStatus;
import com.delivereats.shared.infrastructure.messaging.EventBus;

public class OrderSubscriber {
	private final OrderApplicationService orderApplicationService;

	public OrderSubscriber(EventBus eventBus, OrderApplicationService orderApplicationService) {
		this.orderApplicationService = orderApplicationService;

		eventBus.subscribe("kitchen.confirmed", KitchenConfirmedEvent.class, this::onKitchenConfirmed, "order-service");
	}

	private void onKitchenConfirmed(KitchenConfirmedEvent event) {
		System.out.println(
				"************************* [Order] ************************* Received kitchen confirmed event: "
						+ event);

		orderApplicationService.updateOrderStatus(event.orderId(), OrderStatus.KITCHEN_CONFIRMED);
	}

}
