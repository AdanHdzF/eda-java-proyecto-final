package com.delivereats.rider.domain.model;

public class Assignment {

    private String id;
    private String orderId;
    private String riderId;
    private String riderName;
    private String status;

    public Assignment() {
    }

    public Assignment(String id, String orderId, String riderId, String riderName, String status) {
        this.id = id;
        this.orderId = orderId;
        this.riderId = riderId;
        this.riderName = riderName;
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getRiderId() {
        return riderId;
    }

    public void setRiderId(String riderId) {
        this.riderId = riderId;
    }

    public String getRiderName() {
        return riderName;
    }

    public void setRiderName(String riderName) {
        this.riderName = riderName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
