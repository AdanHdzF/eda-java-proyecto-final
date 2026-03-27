package com.delivereats.kitchen.infrastructure.adapter.out.rest;

import com.delivereats.kitchen.application.dto.PaymentRequest;
import com.delivereats.kitchen.application.dto.PaymentResponse;
import com.delivereats.kitchen.domain.exception.KitchenException;
import com.delivereats.kitchen.domain.port.out.PaymentPort;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class HttpPaymentAdapter implements PaymentPort {

    private final String paymentServiceUrl;
    private final ObjectMapper objectMapper;
    private final HttpClient httpClient;

    public HttpPaymentAdapter(String paymentServiceUrl, ObjectMapper objectMapper) {
        this.paymentServiceUrl = paymentServiceUrl;
        this.objectMapper = objectMapper;
        this.httpClient = HttpClient.newBuilder().version(HttpClient.Version.HTTP_1_1).build();
    }

    @Override
    public PaymentResponse requestPayment(PaymentRequest request) {
        try {
            String json = objectMapper.writeValueAsString(request);
            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .uri(URI.create(paymentServiceUrl + "/api/payments/process"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();

            System.out.println("[Kitchen] Calling Payment Service at: " + paymentServiceUrl + "/api/payments/process");
            HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() >= 200 && response.statusCode() < 300) {
                return objectMapper.readValue(response.body(), PaymentResponse.class);
            } else {
                throw new KitchenException("Payment service returned status: " + response.statusCode());
            }
        } catch (KitchenException e) {
            throw e;
        } catch (Exception e) {
            throw new KitchenException("Error calling payment service: " + e.getMessage(), e);
        }
    }
}
