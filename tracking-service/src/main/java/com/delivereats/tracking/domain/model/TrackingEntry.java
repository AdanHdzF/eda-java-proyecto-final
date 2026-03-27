package com.delivereats.tracking.domain.model;

import java.util.UUID;

public class TrackingEntry {

    private UUID id;
    private String orderId;
    private TrackingStatus status;
    private String riderName;
    private String estimatedArrival;
    private String trackingNumber;

    public TrackingEntry(UUID id, String orderId, TrackingStatus status, String riderName, String estimatedArrival, String trackingNumber) {
        this.id = id;
        this.orderId = orderId;
        this.status = status;
        this.riderName = riderName;
        this.estimatedArrival = estimatedArrival;
        this.trackingNumber = trackingNumber;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }

    public TrackingStatus getStatus() { return status; }
    public void setStatus(TrackingStatus status) { this.status = status; }

    public String getRiderName() { return riderName; }
    public void setRiderName(String riderName) { this.riderName = riderName; }

    public String getEstimatedArrival() { return estimatedArrival; }
    public void setEstimatedArrival(String estimatedArrival) { this.estimatedArrival = estimatedArrival; }

    public String getTrackingNumber() { return trackingNumber; }
    public void setTrackingNumber(String trackingNumber) { this.trackingNumber = trackingNumber; }
}
