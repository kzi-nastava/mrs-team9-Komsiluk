package com.komsiluk.taxi.data.remote.ride;

import java.util.List;

public class StopRideRequestDTO {

    private String stopAddress;
    private List<String> visitedStops;
    private double distanceTravelledKm;

    public StopRideRequestDTO(String stopAddress, List<String> visitedStops, double distanceTravelledKm) {
        this.stopAddress = stopAddress;
        this.visitedStops = visitedStops;
        this.distanceTravelledKm = distanceTravelledKm;
    }

    public String getStopAddress() {
        return stopAddress;
    }

    public void setStopAddress(String stopAddress) {
        this.stopAddress = stopAddress;
    }

    public List<String> getVisitedStops() {
        return visitedStops;
    }

    public void setVisitedStops(List<String> visitedStops) {
        this.visitedStops = visitedStops;
    }

    public double getDistanceTravelledKm() {
        return distanceTravelledKm;
    }

    public void setDistanceTravelledKm(double distanceTravelledKm) {
        this.distanceTravelledKm = distanceTravelledKm;
    }
}