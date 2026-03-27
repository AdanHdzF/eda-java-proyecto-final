package com.delivereats.notification.infrastructure.adapter.out.event;

import com.delivereats.shared.infrastructure.messaging.EventBus;

public class NotificationPublisher {
	private final EventBus eventBus;

	public NotificationPublisher(EventBus eventBus) {
		this.eventBus = eventBus;
	}

	public void publish(String topic, Object event) {
		eventBus.publish(topic, event);
	}

}
