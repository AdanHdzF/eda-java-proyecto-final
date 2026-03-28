package com.delivereats.rider.infrastructure.config;

import java.net.URI;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.internal.inject.AbstractBinder;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.ResourceConfig;

import com.delivereats.rider.application.service.RiderApplicationService;
import com.delivereats.rider.domain.port.in.AssignRiderUseCase;
import com.delivereats.rider.domain.port.in.GetRiderStatusUseCase;
import com.delivereats.rider.infrastructure.adapter.in.event.RiderSubscriber;
import com.delivereats.rider.infrastructure.adapter.in.rest.RiderResource;
import com.delivereats.rider.infrastructure.adapter.out.event.RiderPublisher;
import com.delivereats.rider.infrastructure.adapter.out.persistence.InMemoryAssignmentRepository;
import com.delivereats.rider.infrastructure.adapter.out.persistence.InMemoryRiderRepository;
import com.delivereats.rider.infrastructure.adapter.out.rest.HttpNotificationAdapter;
import com.delivereats.shared.infrastructure.messaging.EventBus;
import com.delivereats.shared.infrastructure.messaging.KafkaEventBus;

public class RiderApplication {

	public static void main(String[] args) throws Exception {
		InMemoryRiderRepository riderRepository = new InMemoryRiderRepository();
		InMemoryAssignmentRepository assignmentRepository = new InMemoryAssignmentRepository();
		HttpNotificationAdapter notificationAdapter = new HttpNotificationAdapter();

		// ── Event bus ──
		EventBus eventBus = new KafkaEventBus();
		RiderPublisher riderPublisher = new RiderPublisher(eventBus);

		RiderApplicationService riderService = new RiderApplicationService(
				riderRepository, assignmentRepository, notificationAdapter, riderPublisher);

		new RiderSubscriber(eventBus, riderService);

		ResourceConfig config = new ResourceConfig();
		config.register(RiderResource.class);
		config.register(JacksonFeature.class);
		config.register(new JacksonConfig());
		config.register(new AbstractBinder() {
			@Override
			protected void configure() {
				bind(riderService).to(AssignRiderUseCase.class);
				bind(riderService).to(GetRiderStatusUseCase.class);
			}
		});

		URI uri = URI.create("http://0.0.0.0:8080/");
		HttpServer server = GrizzlyHttpServerFactory.createHttpServer(uri, config);

		Runtime.getRuntime().addShutdownHook(new Thread(server::shutdownNow));

		System.out.println("Rider Service started at " + uri);
		Thread.currentThread().join();
	}
}
