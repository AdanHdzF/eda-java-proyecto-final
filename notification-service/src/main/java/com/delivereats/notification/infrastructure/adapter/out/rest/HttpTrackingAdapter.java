package com.delivereats.notification.infrastructure.adapter.out.rest;

import com.delivereats.notification.application.dto.CreateTrackingRequest;
import com.delivereats.notification.application.dto.TrackingResponse;
import com.delivereats.notification.domain.port.out.TrackingPort;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class HttpTrackingAdapter implements TrackingPort {

    private final String trackingServiceUrl;
    private final ObjectMapper objectMapper;
    private final HttpClient httpClient;

    public HttpTrackingAdapter(String trackingServiceUrl, ObjectMapper objectMapper) {
        this.trackingServiceUrl = trackingServiceUrl;
        this.objectMapper = objectMapper;
        this.httpClient = HttpClient.newBuilder().version(HttpClient.Version.HTTP_1_1).build();
    }

    @Override
    public TrackingResponse createTracking(CreateTrackingRequest request) {
        try {
            String json = objectMapper.writeValueAsString(request);
            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .uri(URI.create(trackingServiceUrl + "/api/tracking/create"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();

            System.out.println("[Notification] Calling Tracking Service at: " + trackingServiceUrl + "/api/tracking/create");
            HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() >= 200 && response.statusCode() < 300) {
                return objectMapper.readValue(response.body(), TrackingResponse.class);
            } else {
                throw new RuntimeException("Tracking service returned status: " + response.statusCode());
            }
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Error calling tracking service: " + e.getMessage(), e);
        }
    }
}
