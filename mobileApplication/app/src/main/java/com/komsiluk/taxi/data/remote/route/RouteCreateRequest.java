package com.komsiluk.taxi.data.remote.route;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RouteCreateRequest {

    @Expose @SerializedName("startAddress")
    private String startAddress;

    @Expose @SerializedName("endAddress")
    private String endAddress;

    // backend dto ima String stops (ne lista) — ok
    @Expose @SerializedName("stops")
    private String stops;

    @Expose @SerializedName("distanceKm")
    private double distanceKm;

    @Expose @SerializedName("estimatedDurationMin")
    private Integer estimatedDurationMin;

    public String getStartAddress() { return startAddress; }
    public void setStartAddress(String startAddress) { this.startAddress = startAddress; }

    public String getEndAddress() { return endAddress; }
    public void setEndAddress(String endAddress) { this.endAddress = endAddress; }

    public String getStops() { return stops; }
    public void setStops(String stops) { this.stops = stops; }

    public double getDistanceKm() { return distanceKm; }
    public void setDistanceKm(double distanceKm) { this.distanceKm = distanceKm; }

    public Integer getEstimatedDurationMin() { return estimatedDurationMin; }
    public void setEstimatedDurationMin(Integer estimatedDurationMin) { this.estimatedDurationMin = estimatedDurationMin; }
}
