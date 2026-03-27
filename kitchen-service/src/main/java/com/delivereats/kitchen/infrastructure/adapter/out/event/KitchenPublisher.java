package com.delivereats.kitchen.infrastructure.adapter.out.event;

import com.delivereats.shared.infrastructure.messaging.EventBus;

public class KitchenPublisher {
	private final EventBus eventBus;

	public KitchenPublisher(EventBus eventBus) {
		this.eventBus = eventBus;
	}

	public void publish(String topic, Object event) {
		eventBus.publish(topic, event);
	}

}
