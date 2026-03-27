package com.delivereats.order.infrastructure.adapter.out.rest;

import com.delivereats.order.application.dto.KitchenConfirmationRequest;
import com.delivereats.order.application.dto.KitchenConfirmationResponse;
import com.delivereats.order.domain.port.out.KitchenPort;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class HttpKitchenAdapter implements KitchenPort {

    private final String kitchenServiceUrl;
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public HttpKitchenAdapter(String kitchenServiceUrl) {
        this.kitchenServiceUrl = kitchenServiceUrl;
        this.httpClient = HttpClient.newBuilder().version(HttpClient.Version.HTTP_1_1).build();
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public KitchenConfirmationResponse requestConfirmation(KitchenConfirmationRequest request) {
        try {
            // ── Simulate processing delay (5 seconds) ──
            System.out.println("[HttpKitchenAdapter] Simulating 5s processing before calling Kitchen Service...");
            Thread.sleep(5000);

            String jsonBody = objectMapper.writeValueAsString(request);

            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .uri(URI.create(kitchenServiceUrl + "/api/kitchen/confirm"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();

            System.out.println("[HttpKitchenAdapter] Sending POST to " + kitchenServiceUrl + "/api/kitchen/confirm");

            HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                throw new RuntimeException("Kitchen Service returned status " + response.statusCode());
            }

            return objectMapper.readValue(response.body(), KitchenConfirmationResponse.class);

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Interrupted while calling Kitchen Service", e);
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Failed to call Kitchen Service: " + e.getMessage(), e);
        }
    }
}
