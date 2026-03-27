package com.delivereats.tracking.infrastructure.config;

import java.net.URI;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.ResourceConfig;

import com.delivereats.tracking.application.service.TrackingApplicationService;
import com.delivereats.tracking.domain.port.out.TrackingRepositoryPort;
import com.delivereats.tracking.infrastructure.adapter.in.rest.TrackingResource;
import com.delivereats.tracking.infrastructure.adapter.out.persistence.InMemoryTrackingRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

public class TrackingApplication {

	private static final String BASE_URI = "http://0.0.0.0:8080/";

	public static void main(String[] args) {
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.registerModule(new JavaTimeModule());
		objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

		TrackingRepositoryPort repository = new InMemoryTrackingRepository();
		TrackingApplicationService trackingService = new TrackingApplicationService(repository);

		TrackingResource trackingResource = new TrackingResource(trackingService, trackingService);

		ResourceConfig config = new ResourceConfig();
		config.register(trackingResource);
		config.register(JacksonFeature.class);
		config.register(new JacksonConfig());

		HttpServer server = GrizzlyHttpServerFactory.createHttpServer(URI.create(BASE_URI), config);

		System.out.println("===========================================");
		System.out.println("  Tracking Service started at " + BASE_URI);
		System.out.println("  END OF CHAIN - No downstream services");
		System.out.println("===========================================");

		Runtime.getRuntime().addShutdownHook(new Thread(server::shutdownNow));
	}
}
