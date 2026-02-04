package com.komsiluk.taxi.data.remote.add_driver;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class VehicleCreate {

    @SerializedName("model")
    @Expose
    private String model;

    @SerializedName("type")
    @Expose
    private String type;

    @SerializedName("licencePlate")
    @Expose
    private String licencePlate;

    @SerializedName("seatCount")
    @Expose
    private int seatCount;

    @SerializedName("babyFriendly")
    @Expose
    private boolean babyFriendly;

    @SerializedName("petFriendly")
    @Expose
    private boolean petFriendly;

    public VehicleCreate() {}

    public VehicleCreate(String model, String type, String licencePlate, int seatCount,
                         boolean babyFriendly, boolean petFriendly) {
        this.model = model;
        this.type = type;
        this.licencePlate = licencePlate;
        this.seatCount = seatCount;
        this.babyFriendly = babyFriendly;
        this.petFriendly = petFriendly;
    }

    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getLicencePlate() { return licencePlate; }
    public void setLicencePlate(String licencePlate) { this.licencePlate = licencePlate; }

    public int getSeatCount() { return seatCount; }
    public void setSeatCount(int seatCount) { this.seatCount = seatCount; }

    public boolean isBabyFriendly() { return babyFriendly; }
    public void setBabyFriendly(boolean babyFriendly) { this.babyFriendly = babyFriendly; }

    public boolean isPetFriendly() { return petFriendly; }
    public void setPetFriendly(boolean petFriendly) { this.petFriendly = petFriendly; }
}
