package com.delivereats.order.infrastructure.config;

import java.net.URI;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.internal.inject.AbstractBinder;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.ResourceConfig;

import com.delivereats.order.application.service.OrderApplicationService;
import com.delivereats.order.domain.port.in.CancelOrderUseCase;
import com.delivereats.order.domain.port.in.CreateOrderUseCase;
import com.delivereats.order.domain.port.in.GetOrderStatusUseCase;
import com.delivereats.order.infrastructure.adapter.in.rest.OrderResource;
import com.delivereats.order.infrastructure.adapter.out.event.OrderPublisher;
import com.delivereats.order.infrastructure.adapter.out.persistence.InMemoryOrderRepository;
import com.delivereats.order.infrastructure.adapter.out.rest.HttpKitchenAdapter;
import com.delivereats.shared.infrastructure.messaging.EventBus;
import com.delivereats.shared.infrastructure.messaging.KafkaEventBus;

public class OrderApplication {

	private static final String BASE_URI = "http://0.0.0.0:8080/";

	public static void main(String[] args) throws Exception {
		// ── Infrastructure adapters ──
		InMemoryOrderRepository repository = new InMemoryOrderRepository();

		String kitchenUrl = System.getenv().getOrDefault("NEXT_SERVICE_URL", "http://kitchen-service:8080");
		HttpKitchenAdapter kitchenAdapter = new HttpKitchenAdapter(kitchenUrl);
		EventBus eventBus = new KafkaEventBus();
		OrderPublisher orderPublisher = new OrderPublisher(eventBus);

		// ── Application service ──
		OrderApplicationService applicationService = new OrderApplicationService(repository, kitchenAdapter,
				orderPublisher);

		// ── Jersey config with HK2 binding ──
		ResourceConfig config = new ResourceConfig();
		config.register(OrderResource.class);
		config.register(JacksonFeature.class);
		config.register(JacksonConfig.class);
		config.register(new AbstractBinder() {
			@Override
			protected void configure() {
				bind(applicationService).to(CreateOrderUseCase.class);
				bind(applicationService).to(GetOrderStatusUseCase.class);
				bind(applicationService).to(CancelOrderUseCase.class);
			}
		});

		// ── Start Grizzly server ──
		HttpServer server = GrizzlyHttpServerFactory.createHttpServer(URI.create(BASE_URI), config);

		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			System.out.println("[OrderService] Shutting down...");
			server.shutdownNow();
		}));

		System.out.println("========================================");
		System.out.println("  DeliverEats Order Service started");
		System.out.println("  " + BASE_URI);
		System.out.println("  Kitchen URL: " + kitchenUrl);
		System.out.println("========================================");

		Thread.currentThread().join();
	}
}
