package com.komsiluk.taxi.data.remote.passenger_ride_history;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RouteResponseDTO {

    @SerializedName("id")
    @Expose
    private Long id;

    @SerializedName("startAddress")
    @Expose
    private String startAddress;

    @SerializedName("endAddress")
    @Expose
    private String endAddress;

    @SerializedName("stops")
    @Expose
    private String stops;

    @SerializedName("distanceKm")
    @Expose
    private double distanceKm;

    @SerializedName("estimatedDurationMin")
    @Expose
    private Integer estimatedDurationMin;

    public RouteResponseDTO() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStartAddress() {
        return startAddress;
    }

    public void setStartAddress(String startAddress) {
        this.startAddress = startAddress;
    }

    public String getEndAddress() {
        return endAddress;
    }

    public void setEndAddress(String endAddress) {
        this.endAddress = endAddress;
    }

    public String getStops() {
        return stops;
    }

    public void setStops(String stops) {
        this.stops = stops;
    }

    public double getDistanceKm() {
        return distanceKm;
    }

    public void setDistanceKm(double distanceKm) {
        this.distanceKm = distanceKm;
    }

    public Integer getEstimatedDurationMin() {
        return estimatedDurationMin;
    }

    public void setEstimatedDurationMin(Integer estimatedDurationMin) {
        this.estimatedDurationMin = estimatedDurationMin;
    }
}
