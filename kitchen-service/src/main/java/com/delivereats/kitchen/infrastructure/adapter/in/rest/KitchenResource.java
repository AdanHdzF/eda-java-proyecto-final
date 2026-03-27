package com.delivereats.kitchen.infrastructure.adapter.in.rest;

import com.delivereats.kitchen.application.dto.KitchenConfirmationRequest;
import com.delivereats.kitchen.application.dto.KitchenConfirmationResponse;
import com.delivereats.kitchen.application.dto.KitchenStatusResponse;
import com.delivereats.kitchen.domain.model.KitchenOrder;
import com.delivereats.kitchen.domain.port.in.ConfirmOrderUseCase;
import com.delivereats.kitchen.domain.port.out.KitchenOrderRepositoryPort;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.Optional;

@Path("/api/kitchen")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class KitchenResource {

    private final ConfirmOrderUseCase confirmOrderUseCase;
    private final KitchenOrderRepositoryPort kitchenOrderRepository;

    public KitchenResource(ConfirmOrderUseCase confirmOrderUseCase, KitchenOrderRepositoryPort kitchenOrderRepository) {
        this.confirmOrderUseCase = confirmOrderUseCase;
        this.kitchenOrderRepository = kitchenOrderRepository;
    }

    @POST
    @Path("/confirm")
    public Response confirmOrder(KitchenConfirmationRequest request) {
        KitchenConfirmationResponse response = confirmOrderUseCase.confirmOrder(request);
        return Response.ok(response).build();
    }

    @GET
    @Path("/{orderId}/status")
    public Response getStatus(@PathParam("orderId") String orderId) {
        Optional<KitchenOrder> order = kitchenOrderRepository.findByOrderId(orderId);
        if (order.isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        KitchenOrder kitchenOrder = order.get();
        KitchenStatusResponse status = new KitchenStatusResponse(
                kitchenOrder.getOrderId(),
                kitchenOrder.getStatus().name(),
                kitchenOrder.getEstimatedMinutes()
        );
        return Response.ok(status).build();
    }
}
