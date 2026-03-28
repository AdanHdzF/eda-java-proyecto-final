package com.delivereats.tracking.infrastructure.adapter.in.event;

import com.delivereats.shared.infrastructure.messaging.EventBus;
import com.delivereats.tracking.application.dto.CreateTrackingRequest;
import com.delivereats.tracking.application.service.TrackingApplicationService;
import com.delivereats.tracking.domain.event.RiderAssignedEvent;

public class TrackingSubscriber {
	private TrackingApplicationService trackingApplicationService;

	public TrackingSubscriber(EventBus eventBus, TrackingApplicationService trackingApplicationService) {
		this.trackingApplicationService = trackingApplicationService;

		eventBus.subscribe("rider.assigned", RiderAssignedEvent.class, this::onRiderAssigned, "tracking-service");
	}

	private void onRiderAssigned(RiderAssignedEvent event) {
		System.out.println(
				"************************* [Tracking] ************************* Received [rider.assigned] event: "
						+ event);

		trackingApplicationService.createTracking(
				new CreateTrackingRequest(event.orderId(), event.riderName(), event.estimatedMinutes() + " minutes"));
	}

}
