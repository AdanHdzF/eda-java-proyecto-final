package com.delivereats.rider.domain.model;

public class Rider {

    private String id;
    private String name;
    private RiderStatus status;
    private String currentLocation;

    public Rider() {
    }

    public Rider(String id, String name, RiderStatus status, String currentLocation) {
        this.id = id;
        this.name = name;
        this.status = status;
        this.currentLocation = currentLocation;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public RiderStatus getStatus() {
        return status;
    }

    public void setStatus(RiderStatus status) {
        this.status = status;
    }

    public String getCurrentLocation() {
        return currentLocation;
    }

    public void setCurrentLocation(String currentLocation) {
        this.currentLocation = currentLocation;
    }
}
