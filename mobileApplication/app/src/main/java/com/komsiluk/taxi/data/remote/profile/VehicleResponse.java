package com.komsiluk.taxi.data.remote.profile;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class VehicleResponse {

    @SerializedName("id")
    @Expose
    private Long id;

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
    private Integer seatCount;

    @SerializedName("babyFriendly")
    @Expose
    private Boolean babyFriendly;

    @SerializedName("petFriendly")
    @Expose
    private Boolean petFriendly;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getLicencePlate() { return licencePlate; }
    public void setLicencePlate(String licencePlate) { this.licencePlate = licencePlate; }

    public Integer getSeatCount() { return seatCount; }
    public void setSeatCount(Integer seatCount) { this.seatCount = seatCount; }

    public Boolean getBabyFriendly() { return babyFriendly; }
    public void setBabyFriendly(Boolean babyFriendly) { this.babyFriendly = babyFriendly; }

    public Boolean getPetFriendly() { return petFriendly; }
    public void setPetFriendly(Boolean petFriendly) { this.petFriendly = petFriendly; }
}
