package com.delivereats.rider.infrastructure.adapter.in.rest;

import com.delivereats.rider.application.dto.AssignRiderRequest;
import com.delivereats.rider.application.dto.AssignRiderResponse;
import com.delivereats.rider.application.dto.RiderStatusResponse;
import com.delivereats.rider.domain.port.in.AssignRiderUseCase;
import com.delivereats.rider.domain.port.in.GetRiderStatusUseCase;

import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/api/riders")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class RiderResource {

    private final AssignRiderUseCase assignRiderUseCase;
    private final GetRiderStatusUseCase getRiderStatusUseCase;

    @Inject
    public RiderResource(AssignRiderUseCase assignRiderUseCase,
                         GetRiderStatusUseCase getRiderStatusUseCase) {
        this.assignRiderUseCase = assignRiderUseCase;
        this.getRiderStatusUseCase = getRiderStatusUseCase;
    }

    @POST
    @Path("/assign")
    public Response assignRider(AssignRiderRequest request) {
        AssignRiderResponse response = assignRiderUseCase.assignRider(request);
        return Response.ok(response).build();
    }

    @GET
    @Path("/{orderId}/status")
    public Response getRiderStatus(@PathParam("orderId") String orderId) {
        RiderStatusResponse response = getRiderStatusUseCase.getRiderStatus(orderId);
        return Response.ok(response).build();
    }
}
