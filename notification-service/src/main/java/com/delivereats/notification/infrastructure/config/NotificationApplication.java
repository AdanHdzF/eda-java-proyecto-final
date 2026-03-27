package com.delivereats.notification.infrastructure.config;

import java.net.URI;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.ResourceConfig;

import com.delivereats.notification.application.service.NotificationApplicationService;
import com.delivereats.notification.domain.port.in.SendNotificationUseCase;
import com.delivereats.notification.domain.port.out.NotificationRepositoryPort;
import com.delivereats.notification.domain.port.out.TrackingPort;
import com.delivereats.notification.infrastructure.adapter.in.rest.NotificationResource;
import com.delivereats.notification.infrastructure.adapter.out.event.NotificationPublisher;
import com.delivereats.notification.infrastructure.adapter.out.persistence.InMemoryNotificationRepository;
import com.delivereats.notification.infrastructure.adapter.out.rest.HttpTrackingAdapter;
import com.delivereats.shared.infrastructure.messaging.EventBus;
import com.delivereats.shared.infrastructure.messaging.KafkaEventBus;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

public class NotificationApplication {

	private static final String BASE_URI = "http://0.0.0.0:8080/";

	public static void main(String[] args) {
		String nextServiceUrl = System.getenv().getOrDefault("NEXT_SERVICE_URL", "http://localhost:8085");

		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.registerModule(new JavaTimeModule());
		objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

		NotificationRepositoryPort repository = new InMemoryNotificationRepository();
		TrackingPort trackingPort = new HttpTrackingAdapter(nextServiceUrl, objectMapper);

		// ── Event bus ──
		EventBus eventBus = new KafkaEventBus();
		NotificationPublisher notificationPublisher = new NotificationPublisher(eventBus);

		SendNotificationUseCase sendNotificationUseCase = new NotificationApplicationService(repository, trackingPort,
				notificationPublisher);

		NotificationResource notificationResource = new NotificationResource(sendNotificationUseCase);

		ResourceConfig config = new ResourceConfig();
		config.register(notificationResource);
		config.register(JacksonFeature.class);
		config.register(new JacksonConfig());

		HttpServer server = GrizzlyHttpServerFactory.createHttpServer(URI.create(BASE_URI), config);

		System.out.println("===========================================");
		System.out.println("  Notification Service started at " + BASE_URI);
		System.out.println("  Tracking Service URL: " + nextServiceUrl);
		System.out.println("===========================================");

		Runtime.getRuntime().addShutdownHook(new Thread(server::shutdownNow));
	}
}
