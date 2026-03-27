package com.delivereats.rider.infrastructure.adapter.out.event;

import com.delivereats.shared.infrastructure.messaging.EventBus;

public class RiderPublisher {
	private final EventBus eventBus;

	public RiderPublisher(EventBus eventBus) {
		this.eventBus = eventBus;
	}

	public void publish(String topic, Object event) {
		eventBus.publish(topic, event);
	}

}
