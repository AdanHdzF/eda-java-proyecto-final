package com.delivereats.payment.infrastructure.adapter.in.rest;

import com.delivereats.payment.application.dto.PaymentRequest;
import com.delivereats.payment.application.dto.PaymentResponse;
import com.delivereats.payment.application.dto.PaymentStatusResponse;
import com.delivereats.payment.application.dto.RefundResponse;
import com.delivereats.payment.domain.port.in.GetPaymentStatusUseCase;
import com.delivereats.payment.domain.port.in.ProcessPaymentUseCase;
import com.delivereats.payment.domain.port.in.RefundPaymentUseCase;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/api/payments")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class PaymentResource {

    private final ProcessPaymentUseCase processPaymentUseCase;
    private final RefundPaymentUseCase refundPaymentUseCase;
    private final GetPaymentStatusUseCase getPaymentStatusUseCase;

    public PaymentResource(ProcessPaymentUseCase processPaymentUseCase,
                           RefundPaymentUseCase refundPaymentUseCase,
                           GetPaymentStatusUseCase getPaymentStatusUseCase) {
        this.processPaymentUseCase = processPaymentUseCase;
        this.refundPaymentUseCase = refundPaymentUseCase;
        this.getPaymentStatusUseCase = getPaymentStatusUseCase;
    }

    @POST
    @Path("/process")
    public Response processPayment(PaymentRequest request) {
        PaymentResponse response = processPaymentUseCase.processPayment(request);
        return Response.ok(response).build();
    }

    @POST
    @Path("/refund/{orderId}")
    public Response refundPayment(@PathParam("orderId") String orderId) {
        RefundResponse response = refundPaymentUseCase.refundPayment(orderId);
        return Response.ok(response).build();
    }

    @GET
    @Path("/{orderId}/status")
    public Response getPaymentStatus(@PathParam("orderId") String orderId) {
        PaymentStatusResponse response = getPaymentStatusUseCase.getPaymentStatus(orderId);
        return Response.ok(response).build();
    }
}
