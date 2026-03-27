package com.delivereats.payment.infrastructure.adapter.out.event;

import com.delivereats.shared.infrastructure.messaging.EventBus;

public class PaymentPublisher {
	private final EventBus eventBus;

	public PaymentPublisher(EventBus eventBus) {
		this.eventBus = eventBus;
	}

	public void publish(String topic, Object event) {
		eventBus.publish(topic, event);
	}

}
