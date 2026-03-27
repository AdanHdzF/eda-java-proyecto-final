package com.delivereats.order.infrastructure.adapter.in.rest;

import com.delivereats.order.application.dto.CreateOrderRequest;
import com.delivereats.order.application.dto.OrderResponse;
import com.delivereats.order.application.dto.OrderStatusDto;
import com.delivereats.order.domain.port.in.CancelOrderUseCase;
import com.delivereats.order.domain.port.in.CreateOrderUseCase;
import com.delivereats.order.domain.port.in.GetOrderStatusUseCase;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Path("/api/orders")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class OrderResource {

    private final CreateOrderUseCase createOrderUseCase;
    private final GetOrderStatusUseCase getOrderStatusUseCase;
    private final CancelOrderUseCase cancelOrderUseCase;
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    @Inject
    public OrderResource(CreateOrderUseCase createOrderUseCase,
                         GetOrderStatusUseCase getOrderStatusUseCase,
                         CancelOrderUseCase cancelOrderUseCase) {
        this.createOrderUseCase = createOrderUseCase;
        this.getOrderStatusUseCase = getOrderStatusUseCase;
        this.cancelOrderUseCase = cancelOrderUseCase;
        this.httpClient = HttpClient.newBuilder().version(HttpClient.Version.HTTP_1_1).build();
        this.objectMapper = new ObjectMapper();
    }

    @POST
    public Response createOrder(CreateOrderRequest request) {
        System.out.println("[OrderResource] POST /api/orders - Creating order (SYNC chain starts)...");
        long start = System.currentTimeMillis();

        OrderResponse response = createOrderUseCase.createOrder(request);

        long elapsed = System.currentTimeMillis() - start;
        System.out.println("[OrderResource] Order created in " + elapsed + "ms (blocking!)");

        return Response.status(Response.Status.CREATED).entity(response).build();
    }

    @GET
    @Path("/{id}")
    public Response getOrder(@PathParam("id") String orderId) {
        OrderStatusDto status = getOrderStatusUseCase.getOrderStatus(orderId);
        return Response.ok(status).build();
    }

    @GET
    @Path("/{id}/status")
    public Response getFullOrderStatus(@PathParam("id") String orderId) {
        System.out.println("[OrderResource] GET /api/orders/" + orderId + "/status - Calling ALL downstream services (SYNC)...");
        long start = System.currentTimeMillis();

        // First get local order data
        OrderStatusDto localStatus = getOrderStatusUseCase.getOrderStatus(orderId);

        // ── Call each downstream service SYNCHRONOUSLY ──
        String kitchenStatus = callDownstream("http://kitchen-service:8080/api/kitchen/" + orderId + "/status", "kitchenStatus");
        String paymentStatus = callDownstream("http://payment-service:8080/api/payments/" + orderId + "/status", "paymentStatus");
        String riderName = callDownstream("http://rider-service:8080/api/riders/" + orderId + "/status", "riderName");
        String trackingNumber = callDownstream("http://tracking-service:8080/api/tracking/" + orderId, "trackingNumber");

        // Derive the real status from downstream responses
        String derivedStatus = localStatus.status();
        if (trackingNumber != null && !trackingNumber.equals("UNAVAILABLE")) {
            derivedStatus = "TRACKING";
        } else if (riderName != null && !riderName.equals("UNAVAILABLE")) {
            derivedStatus = "RIDER_ASSIGNED";
        } else if (paymentStatus != null && !paymentStatus.equals("UNAVAILABLE") && paymentStatus.contains("COMPLETED")) {
            derivedStatus = "PAID";
        } else if (kitchenStatus != null && !kitchenStatus.equals("UNAVAILABLE") && kitchenStatus.contains("CONFIRMED")) {
            derivedStatus = "KITCHEN_CONFIRMED";
        }

        OrderStatusDto fullStatus = new OrderStatusDto(
                localStatus.orderId(),
                localStatus.customerName(),
                localStatus.restaurantName(),
                derivedStatus,
                localStatus.totalAmount(),
                kitchenStatus,
                paymentStatus,
                riderName,
                trackingNumber
        );

        long elapsed = System.currentTimeMillis() - start;
        System.out.println("[OrderResource] Full status assembled in " + elapsed + "ms (blocking!)");

        return Response.ok(fullStatus).build();
    }

    @DELETE
    @Path("/{id}")
    public Response cancelOrder(@PathParam("id") String orderId, @QueryParam("reason") String reason) {
        cancelOrderUseCase.cancelOrder(orderId, reason != null ? reason : "No reason provided");
        return Response.noContent().build();
    }

    private String callDownstream(String url, String fieldName) {
        try {
            System.out.println("[OrderResource] Calling " + url + " ...");
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Accept", "application/json")
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                JsonNode json = objectMapper.readTree(response.body());
                if (json.has(fieldName)) {
                    return json.get(fieldName).asText();
                }
                return response.body();
            }
            return "UNAVAILABLE";
        } catch (Exception e) {
            System.out.println("[OrderResource] Failed to call " + url + ": " + e.getMessage());
            return "UNAVAILABLE";
        }
    }
}
