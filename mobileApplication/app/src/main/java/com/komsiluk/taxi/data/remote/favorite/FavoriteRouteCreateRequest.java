package com.komsiluk.taxi.data.remote.favorite;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class FavoriteRouteCreateRequest {

    @Expose @SerializedName("title")
    private String title;

    @Expose @SerializedName("routeId")
    private Long routeId;

    @Expose @SerializedName("passengersEmails")
    private List<String> passengersEmails;

    @Expose @SerializedName("vehicleType")
    private String vehicleType;

    @Expose @SerializedName("petFriendly")
    private boolean petFriendly;

    @Expose @SerializedName("babyFriendly")
    private boolean babyFriendly;

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public Long getRouteId() { return routeId; }
    public void setRouteId(Long routeId) { this.routeId = routeId; }

    public List<String> getPassengersEmails() { return passengersEmails; }
    public void setPassengersEmails(List<String> passengersEmails) { this.passengersEmails = passengersEmails; }

    public String getVehicleType() { return vehicleType; }
    public void setVehicleType(String vehicleType) { this.vehicleType = vehicleType; }

    public boolean isPetFriendly() { return petFriendly; }
    public void setPetFriendly(boolean petFriendly) { this.petFriendly = petFriendly; }

    public boolean isBabyFriendly() { return babyFriendly; }
    public void setBabyFriendly(boolean babyFriendly) { this.babyFriendly = babyFriendly; }
}
