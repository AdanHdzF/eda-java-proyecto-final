package com.delivereats.tracking.infrastructure.adapter.in.rest;

import com.delivereats.tracking.application.dto.CreateTrackingRequest;
import com.delivereats.tracking.application.dto.TrackingResponse;
import com.delivereats.tracking.domain.port.in.CreateTrackingUseCase;
import com.delivereats.tracking.domain.port.in.GetTrackingUseCase;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/api/tracking")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class TrackingResource {

    private final CreateTrackingUseCase createTrackingUseCase;
    private final GetTrackingUseCase getTrackingUseCase;

    public TrackingResource(CreateTrackingUseCase createTrackingUseCase, GetTrackingUseCase getTrackingUseCase) {
        this.createTrackingUseCase = createTrackingUseCase;
        this.getTrackingUseCase = getTrackingUseCase;
    }

    @POST
    @Path("/create")
    public Response createTracking(CreateTrackingRequest request) {
        TrackingResponse response = createTrackingUseCase.createTracking(request);
        return Response.ok(response).build();
    }

    @GET
    @Path("/{orderId}")
    public Response getTracking(@PathParam("orderId") String orderId) {
        TrackingResponse response = getTrackingUseCase.getTracking(orderId);
        return Response.ok(response).build();
    }
}
