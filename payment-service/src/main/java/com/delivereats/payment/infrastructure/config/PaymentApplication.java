package com.delivereats.payment.infrastructure.config;

import java.net.URI;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.ResourceConfig;

import com.delivereats.payment.application.service.PaymentApplicationService;
import com.delivereats.payment.domain.port.out.PaymentRepositoryPort;
import com.delivereats.payment.domain.port.out.RiderPort;
import com.delivereats.payment.infrastructure.adapter.in.event.PaymentSubscriber;
import com.delivereats.payment.infrastructure.adapter.in.rest.PaymentResource;
import com.delivereats.payment.infrastructure.adapter.out.event.PaymentPublisher;
import com.delivereats.payment.infrastructure.adapter.out.persistence.InMemoryPaymentRepository;
import com.delivereats.payment.infrastructure.adapter.out.rest.HttpRiderAdapter;
import com.delivereats.shared.infrastructure.messaging.EventBus;
import com.delivereats.shared.infrastructure.messaging.KafkaEventBus;

public class PaymentApplication {

	public static void main(String[] args) throws Exception {
		String nextServiceUrl = System.getenv().getOrDefault("NEXT_SERVICE_URL", "http://localhost:8083");

		// Infrastructure adapters
		PaymentRepositoryPort repository = new InMemoryPaymentRepository();
		RiderPort riderPort = new HttpRiderAdapter(nextServiceUrl);

		// ── Event bus ──
		EventBus eventBus = new KafkaEventBus();
		PaymentPublisher paymentPublisher = new PaymentPublisher(eventBus);

		// Application service
		PaymentApplicationService service = new PaymentApplicationService(repository, riderPort, paymentPublisher);

		new PaymentSubscriber(eventBus, service);

		// REST resource
		PaymentResource resource = new PaymentResource(service, service, service);

		// Jersey config
		ResourceConfig config = new ResourceConfig();
		config.register(resource);
		config.register(JacksonFeature.class);
		config.register(new JacksonConfig());

		String uri = "http://0.0.0.0:8080/";
		HttpServer server = GrizzlyHttpServerFactory.createHttpServer(URI.create(uri), config);

		System.out.println("===========================================");
		System.out.println("  Payment Service started");
		System.out.println("  " + uri);
		System.out.println("  Rider Service URL: " + nextServiceUrl);
		System.out.println("===========================================");

		Runtime.getRuntime().addShutdownHook(new Thread(server::shutdownNow));
		Thread.currentThread().join();
	}
}
