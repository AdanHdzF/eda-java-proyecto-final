package com.delivereats.payment.infrastructure.adapter.out.rest;

import com.delivereats.payment.application.dto.AssignRiderRequest;
import com.delivereats.payment.application.dto.AssignRiderResponse;
import com.delivereats.payment.domain.port.out.RiderPort;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class HttpRiderAdapter implements RiderPort {

    private final String riderServiceUrl;
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public HttpRiderAdapter(String riderServiceUrl) {
        this.riderServiceUrl = riderServiceUrl;
        this.httpClient = HttpClient.newBuilder().version(HttpClient.Version.HTTP_1_1).build();
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public AssignRiderResponse requestRider(AssignRiderRequest request) {
        try {
            String json = objectMapper.writeValueAsString(request);

            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .uri(URI.create(riderServiceUrl + "/api/riders/assign"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();

            System.out.println("[HttpRiderAdapter] POST " + riderServiceUrl + "/api/riders/assign");

            HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() >= 200 && response.statusCode() < 300) {
                return objectMapper.readValue(response.body(), AssignRiderResponse.class);
            } else {
                throw new RuntimeException("Rider service returned status: " + response.statusCode()
                        + " body: " + response.body());
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to call rider service: " + e.getMessage(), e);
        }
    }
}
