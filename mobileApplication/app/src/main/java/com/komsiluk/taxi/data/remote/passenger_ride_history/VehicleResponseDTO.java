package com.komsiluk.taxi.data.remote.passenger_ride_history;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class VehicleResponseDTO {

    @SerializedName("id")
    @Expose
    private Long id;

    @SerializedName("make")
    @Expose
    private String make;

    @SerializedName("model")
    @Expose
    private String model;

    @SerializedName("licensePlate")
    @Expose
    private String licensePlate;

    @SerializedName("vehicleType")
    @Expose
    private VehicleType vehicleType;

    @SerializedName("babyFriendly")
    @Expose
    private boolean babyFriendly;

    @SerializedName("petFriendly")
    @Expose
    private boolean petFriendly;

    public VehicleResponseDTO() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMake() {
        return make;
    }

    public void setMake(String make) {
        this.make = make;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getLicensePlate() {
        return licensePlate;
    }

    public void setLicensePlate(String licensePlate) {
        this.licensePlate = licensePlate;
    }

    public VehicleType getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(VehicleType vehicleType) {
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
}
