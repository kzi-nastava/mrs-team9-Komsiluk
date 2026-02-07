package com.komsiluk.taxi.data.remote.ride;

import com.komsiluk.taxi.data.remote.passenger_ride_history.DriverResponseDTO;
import com.komsiluk.taxi.data.remote.passenger_ride_history.RouteResponseDTO;

import java.util.List;

public class AdminRideDetails {
    private Long rideId;
    private String status; // Koristimo String za RideStatus
    private RouteResponseDTO route;
    private String startTime; // LocalDateTime se najbolje parsira kao String ili Long
    private String endTime;

    private DriverResponseDTO driver;
    private List<String> passengerEmails;
    private Long creatorId;

    private double distanceKm;
    private int estimatedDurationMin;
    private boolean panicTriggered;

    public Long getRideId() { return rideId; }
    public String getStatus() { return status; }
    public RouteResponseDTO getRoute() { return route; }
    public DriverResponseDTO getDriver() { return driver; }
    public List<String> getPassengerEmails() { return passengerEmails; }
    public double getDistanceKm() { return distanceKm; }
    public int getEstimatedDurationMin() { return estimatedDurationMin; }

    public void setRideId(Long rideId) {
        this.rideId = rideId;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setRoute(RouteResponseDTO route) {
        this.route = route;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public void setDriver(DriverResponseDTO driver) {
        this.driver = driver;
    }

    public void setPassengerEmails(List<String> passengerEmails) {
        this.passengerEmails = passengerEmails;
    }

    public Long getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(Long creatorId) {
        this.creatorId = creatorId;
    }

    public void setDistanceKm(double distanceKm) {
        this.distanceKm = distanceKm;
    }

    public void setEstimatedDurationMin(int estimatedDurationMin) {
        this.estimatedDurationMin = estimatedDurationMin;
    }

    public boolean isPanicTriggered() {
        return panicTriggered;
    }

    public void setPanicTriggered(boolean panicTriggered) {
        this.panicTriggered = panicTriggered;
    }
}
