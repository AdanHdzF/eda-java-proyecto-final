package com.delivereats.rider.infrastructure.adapter.out.rest;

import com.delivereats.rider.application.dto.NotificationRequest;
import com.delivereats.rider.application.dto.NotificationResponse;
import com.delivereats.rider.domain.port.out.NotificationPort;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class HttpNotificationAdapter implements NotificationPort {

    private final String baseUrl;
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public HttpNotificationAdapter() {
        this.baseUrl = System.getenv().getOrDefault("NEXT_SERVICE_URL", "http://notification-service:8080");
        this.httpClient = HttpClient.newBuilder().version(HttpClient.Version.HTTP_1_1).build();
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
        this.objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @Override
    public NotificationResponse sendNotification(NotificationRequest request) {
        try {
            String json = objectMapper.writeValueAsString(request);

            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + "/api/notifications/send"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();

            HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

            return objectMapper.readValue(response.body(), NotificationResponse.class);
        } catch (Exception e) {
            throw new RuntimeException("Error calling notification service: " + e.getMessage(), e);
        }
    }
}
