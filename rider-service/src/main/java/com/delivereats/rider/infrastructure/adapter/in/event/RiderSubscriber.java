package com.delivereats.rider.infrastructure.adapter.in.event;

import com.delivereats.rider.application.dto.AssignRiderRequest;
import com.delivereats.rider.application.service.RiderApplicationService;
import com.delivereats.rider.domain.event.OrderCreatedEvent;
import com.delivereats.shared.infrastructure.messaging.EventBus;

public class RiderSubscriber {
	private final RiderApplicationService riderApplicationService;

	public RiderSubscriber(EventBus eventBus, RiderApplicationService riderApplicationService) {
		this.riderApplicationService = riderApplicationService;

		eventBus.subscribe("orders.created",
				OrderCreatedEvent.class, this::onOrderCreated, "payment-service");
	}

	private void onOrderCreated(OrderCreatedEvent event) {
		System.out.println(
				"************************* [Rider] ************************* Received [orders.created] event: "
						+ event);

		riderApplicationService.assignRider(
				new AssignRiderRequest(event.orderId(), event.restaurantName()));
	}

}
