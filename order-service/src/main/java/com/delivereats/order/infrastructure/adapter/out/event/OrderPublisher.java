package com.delivereats.order.infrastructure.adapter.out.event;

import com.delivereats.shared.infrastructure.messaging.EventBus;

public class OrderPublisher {
	private final EventBus eventBus;

	public OrderPublisher(EventBus eventBus) {
		this.eventBus = eventBus;
	}

	public void publish(String topic, Object event) {
		eventBus.publish(topic, event);
	}

}
