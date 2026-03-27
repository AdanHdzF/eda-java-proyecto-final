package com.delivereats.kitchen.infrastructure.config;

import java.net.URI;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.ResourceConfig;

import com.delivereats.kitchen.application.service.KitchenApplicationService;
import com.delivereats.kitchen.domain.port.in.ConfirmOrderUseCase;
import com.delivereats.kitchen.domain.port.out.KitchenOrderRepositoryPort;
import com.delivereats.kitchen.domain.port.out.PaymentPort;
import com.delivereats.kitchen.infrastructure.adapter.in.rest.KitchenResource;
import com.delivereats.kitchen.infrastructure.adapter.out.event.KitchenPublisher;
import com.delivereats.kitchen.infrastructure.adapter.out.persistence.InMemoryKitchenOrderRepository;
import com.delivereats.kitchen.infrastructure.adapter.out.rest.HttpPaymentAdapter;
import com.delivereats.shared.infrastructure.messaging.EventBus;
import com.delivereats.shared.infrastructure.messaging.KafkaEventBus;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

public class KitchenApplication {

	private static final String BASE_URI = "http://0.0.0.0:8080/";

	public static void main(String[] args) {
		String nextServiceUrl = System.getenv().getOrDefault("NEXT_SERVICE_URL", "http://localhost:8082");

		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.registerModule(new JavaTimeModule());
		objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

		KitchenOrderRepositoryPort repository = new InMemoryKitchenOrderRepository();
		PaymentPort paymentPort = new HttpPaymentAdapter(nextServiceUrl, objectMapper);

		// ── Event bus ──
		EventBus eventBus = new KafkaEventBus();
		KitchenPublisher kitchenPublisher = new KitchenPublisher(eventBus);

		ConfirmOrderUseCase confirmOrderUseCase = new KitchenApplicationService(repository, paymentPort,
				kitchenPublisher);

		KitchenResource kitchenResource = new KitchenResource(confirmOrderUseCase, repository);

		ResourceConfig config = new ResourceConfig();
		config.register(kitchenResource);
		config.register(JacksonFeature.class);
		config.register(new JacksonConfig());

		HttpServer server = GrizzlyHttpServerFactory.createHttpServer(URI.create(BASE_URI), config);

		System.out.println("===========================================");
		System.out.println("  Kitchen Service started at " + BASE_URI);
		System.out.println("  Payment Service URL: " + nextServiceUrl);
		System.out.println("===========================================");

		Runtime.getRuntime().addShutdownHook(new Thread(server::shutdownNow));
	}
}
