package com.delivereats.notification.infrastructure.adapter.in.event;

import com.delivereats.notification.application.dto.NotificationRequest;
import com.delivereats.notification.domain.event.RiderAssignedEvent;
import com.delivereats.notification.domain.port.in.SendNotificationUseCase;
import com.delivereats.shared.infrastructure.messaging.EventBus;

public class NotificationSubscriber {
	private SendNotificationUseCase sendNotificationUseCase;

	public NotificationSubscriber(EventBus eventBus, SendNotificationUseCase sendNotificationUseCase) {
		this.sendNotificationUseCase = sendNotificationUseCase;

		eventBus.subscribe("rider.assigned", RiderAssignedEvent.class, this::onRiderAssigned, "notification-service");

	}

	private void onRiderAssigned(RiderAssignedEvent event) {
		System.out.println(
				"************************* [Notification] ************************* Received [rider.assigned] event: "
						+ event);

		sendNotificationUseCase
				.sendNotification(new NotificationRequest(event.orderId(), "RIDER_ASSIGNED", event.riderName(),
						"Your rider " + event.riderName() + " has been assigned and will arrive in approximately "
								+ event.estimatedMinutes() + " minutes."));
	}

}
