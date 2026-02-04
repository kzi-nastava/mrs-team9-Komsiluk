package com.komsiluk.taxi.data.remote.ride;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class RideCreateRequest {

    @SerializedName("creatorId")
    @Expose
    private Long creatorId;

    @SerializedName("startAddress")
    @Expose
    private String startAddress;

    @SerializedName("endAddress")
    @Expose
    private String endAddress;

    @SerializedName("stops")
    @Expose
    private List<String> stops;

    @SerializedName("distanceKm")
    @Expose
    private double distanceKm;

    @SerializedName("estimatedDurationMin")
    @Expose
    private int estimatedDurationMin;

    @SerializedName("startLat")
    @Expose
    private Double startLat;

    @SerializedName("startLng")
    @Expose
    private Double startLng;

    @SerializedName("vehicleType")
    @Expose
    private String vehicleType;

    @SerializedName("babyFriendly")
    @Expose
    private boolean babyFriendly;

    @SerializedName("petFriendly")
    @Expose
    private boolean petFriendly;

    @SerializedName("scheduledAt")
    @Expose
    private String scheduledAt;

    @SerializedName("passengerEmails")
    @Expose
    private List<String> passengerEmails;

    public RideCreateRequest() {
    }

    public Long getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(Long creatorId) {
        this.creatorId = creatorId;
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

    public List<String> getStops() {
        return stops;
    }

    public void setStops(List<String> stops) {
        this.stops = stops;
    }

    public double getDistanceKm() {
        return distanceKm;
    }

    public void setDistanceKm(double distanceKm) {
        this.distanceKm = distanceKm;
    }

    public int getEstimatedDurationMin() {
        return estimatedDurationMin;
    }

    public void setEstimatedDurationMin(int estimatedDurationMin) {
        this.estimatedDurationMin = estimatedDurationMin;
    }

    public Double getStartLat() {
        return startLat;
    }

    public void setStartLat(Double startLat) {
        this.startLat = startLat;
    }

    public Double getStartLng() {
        return startLng;
    }

    public void setStartLng(Double startLng) {
        this.startLng = startLng;
    }

    public String getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
    }

    public boolean isBabyFriendly() {
        return babyFriendly;
    }

    public void setBabyFriendly(boolean babyFriendly) {
        this.babyFriendly = babyFriendly;
    }

    public boolean isPetFriendly() {
        return petFriendly;
    }

    public void setPetFriendly(boolean petFriendly) {
        this.petFriendly = petFriendly;
    }

    public String getScheduledAt() {
        return scheduledAt;
    }

    public void setScheduledAt(String scheduledAt) {
        this.scheduledAt = scheduledAt;
    }

    public List<String> getPassengerEmails() {
        return passengerEmails;
    }

    public void setPassengerEmails(List<String> passengerEmails) {
        this.passengerEmails = passengerEmails;
    }
}
