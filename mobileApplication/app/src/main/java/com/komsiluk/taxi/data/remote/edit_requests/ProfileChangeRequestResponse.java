package com.komsiluk.taxi.data.remote.edit_requests;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ProfileChangeRequestResponse {

    @SerializedName("id") @Expose
    private Long id;

    @SerializedName("requestedAt") @Expose
    private String requestedAt; // LocalDateTime -> dolazi kao string (ISO)

    @SerializedName("status") @Expose
    private String status;

    @SerializedName("newName") @Expose
    private String newName;

    @SerializedName("newSurname") @Expose
    private String newSurname;

    @SerializedName("newAddress") @Expose
    private String newAddress;

    @SerializedName("newCity") @Expose
    private String newCity;

    @SerializedName("newPhoneNumber") @Expose
    private String newPhoneNumber;

    @SerializedName("newProfileImageUrl") @Expose
    private String newProfileImageUrl;

    @SerializedName("newModel") @Expose
    private String newModel;

    @SerializedName("newType") @Expose
    private String newType; // enum sa beka, kao string

    @SerializedName("newLicencePlate") @Expose
    private String newLicencePlate;

    @SerializedName("newSeatCount") @Expose
    private Integer newSeatCount;

    @SerializedName("newBabyFriendly") @Expose
    private Boolean newBabyFriendly;

    @SerializedName("newPetFriendly") @Expose
    private Boolean newPetFriendly;

    @SerializedName("driverId") @Expose
    private Long driverId;

    public Long getId() { return id; }
    public String getRequestedAt() { return requestedAt; }
    public String getStatus() { return status; }

    public String getNewName() { return newName; }
    public String getNewSurname() { return newSurname; }
    public String getNewAddress() { return newAddress; }
    public String getNewCity() { return newCity; }
    public String getNewPhoneNumber() { return newPhoneNumber; }
    public String getNewProfileImageUrl() { return newProfileImageUrl; }

    public String getNewModel() { return newModel; }
    public String getNewType() { return newType; }
    public String getNewLicencePlate() { return newLicencePlate; }
    public Integer getNewSeatCount() { return newSeatCount; }
    public Boolean getNewBabyFriendly() { return newBabyFriendly; }
    public Boolean getNewPetFriendly() { return newPetFriendly; }

    public Long getDriverId() { return driverId; }
}
