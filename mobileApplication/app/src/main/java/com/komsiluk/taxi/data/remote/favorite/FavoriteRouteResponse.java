package com.komsiluk.taxi.data.remote.favorite;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class FavoriteRouteResponse {

    @Expose @SerializedName("id")
    private Long id;

    @Expose @SerializedName("title")
    private String title;

    @Expose @SerializedName("routeId")
    private Long routeId;

    @Expose @SerializedName("startAddress")
    private String startAddress;

    @Expose @SerializedName("endAddress")
    private String endAddress;

    @Expose @SerializedName("stops")
    private List<String> stops;

    @Expose @SerializedName("passengerIds")
    private List<Long> passengerIds;

    @Expose @SerializedName("vehicleType")
    private String vehicleType;

    @Expose @SerializedName("petFriendly")
    private boolean petFriendly;

    @Expose @SerializedName("babyFriendly")
    private boolean babyFriendly;

    @Expose @SerializedName("distanceKm")
    private double distanceKm;

    @Expose @SerializedName("estimatedDurationMin")
    private Integer estimatedDurationMin;

    // getters/setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public Long getRouteId() { return routeId; }
    public void setRouteId(Long routeId) { this.routeId = routeId; }

    public String getStartAddress() { return startAddress; }
    public void setStartAddress(String startAddress) { this.startAddress = startAddress; }

    public String getEndAddress() { return endAddress; }
    public void setEndAddress(String endAddress) { this.endAddress = endAddress; }

    public List<String> getStops() { return stops; }
    public void setStops(List<String> stops) { this.stops = stops; }

    public List<Long> getPassengerIds() { return passengerIds; }
    public void setPassengerIds(List<Long> passengerIds) { this.passengerIds = passengerIds; }

    public String getVehicleType() { return vehicleType; }
    public void setVehicleType(String vehicleType) { this.vehicleType = vehicleType; }

    public boolean isPetFriendly() { return petFriendly; }
    public void setPetFriendly(boolean petFriendly) { this.petFriendly = petFriendly; }

    public boolean isBabyFriendly() { return babyFriendly; }
    public void setBabyFriendly(boolean babyFriendly) { this.babyFriendly = babyFriendly; }

    public double getDistanceKm() { return distanceKm; }
    public void setDistanceKm(double distanceKm) { this.distanceKm = distanceKm; }

    public Integer getEstimatedDurationMin() { return estimatedDurationMin; }
    public void setEstimatedDurationMin(Integer estimatedDurationMin) { this.estimatedDurationMin = estimatedDurationMin; }
}
