package com.delivereats.notification.application.service;

import java.time.Instant;
import java.util.UUID;

import com.delivereats.notification.application.dto.NotificationRequest;
import com.delivereats.notification.application.dto.NotificationResponse;
import com.delivereats.notification.domain.event.NotificationSentEvent;
import com.delivereats.notification.domain.model.Notification;
import com.delivereats.notification.domain.model.NotificationType;
import com.delivereats.notification.domain.port.in.SendNotificationUseCase;
import com.delivereats.notification.domain.port.out.NotificationRepositoryPort;
import com.delivereats.notification.domain.port.out.TrackingPort;
import com.delivereats.notification.infrastructure.adapter.out.event.NotificationPublisher;

public class NotificationApplicationService implements SendNotificationUseCase {

	private final NotificationRepositoryPort notificationRepository;
	private final TrackingPort trackingPort;
	private final NotificationPublisher notificationPublisher;

	public NotificationApplicationService(NotificationRepositoryPort notificationRepository, TrackingPort trackingPort,
			NotificationPublisher notificationPublisher) {
		this.notificationRepository = notificationRepository;
		this.trackingPort = trackingPort;
		this.notificationPublisher = notificationPublisher;
	}

	@Override
	public NotificationResponse sendNotification(NotificationRequest request) {
		System.out.println("[Notification] Processing notification for order: " + request.orderId());

		try {
			Thread.sleep(8000);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			throw new RuntimeException("Interrupted while sending notification", e);
		}

		UUID notificationId = UUID.randomUUID();
		NotificationType type = NotificationType.valueOf(request.type());

		Notification notification = new Notification(
				notificationId,
				request.orderId(),
				type,
				request.customerName(),
				request.message(),
				Instant.now());

		notificationRepository.save(notification);

		System.out.println("\uD83D\uDCE7 Notification sent: " + request.type() + " for order " + request.orderId()
				+ " to " + request.customerName());

		notificationPublisher.publish("notification.sent",
				new NotificationSentEvent(notification.getOrderId(), notification.getType().name(),
						notification.getRecipient()));

		// System.out.println("[Notification] Calling Tracking Service for order: " +
		// request.orderId());
		// TrackingResponse trackingResponse = trackingPort.createTracking(
		// new CreateTrackingRequest(request.orderId(), "Rider-" +
		// request.orderId().substring(0, 4),
		// "30 minutes"));
		// System.out.println("[Notification] Tracking created: " +
		// trackingResponse.trackingNumber());

		return new NotificationResponse(notificationId.toString(), true);
	}
}
