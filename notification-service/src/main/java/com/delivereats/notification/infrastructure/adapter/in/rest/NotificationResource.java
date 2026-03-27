package com.delivereats.notification.infrastructure.adapter.in.rest;

import com.delivereats.notification.application.dto.NotificationRequest;
import com.delivereats.notification.application.dto.NotificationResponse;
import com.delivereats.notification.domain.port.in.SendNotificationUseCase;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/api/notifications")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class NotificationResource {

    private final SendNotificationUseCase sendNotificationUseCase;

    public NotificationResource(SendNotificationUseCase sendNotificationUseCase) {
        this.sendNotificationUseCase = sendNotificationUseCase;
    }

    @POST
    @Path("/send")
    public Response sendNotification(NotificationRequest request) {
        NotificationResponse response = sendNotificationUseCase.sendNotification(request);
        return Response.ok(response).build();
    }
}
