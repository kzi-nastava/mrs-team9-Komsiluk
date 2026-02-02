package com.komsiluk.taxi.data.remote.profile;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ProfileChangeRequestCreate {

    @SerializedName("newName") @Expose
    public String newName;

    @SerializedName("newSurname") @Expose
    public String newSurname;

    @SerializedName("newAddress") @Expose
    public String newAddress;

    @SerializedName("newCity") @Expose
    public String newCity;

    @SerializedName("newPhoneNumber") @Expose
    public String newPhoneNumber;

    @SerializedName("newProfileImageUrl") @Expose
    public String newProfileImageUrl;

    @SerializedName("newModel") @Expose
    public String newModel;

    @SerializedName("newType") @Expose
    public String newType; // saljemo kao String enum-a

    @SerializedName("newLicencePlate") @Expose
    public String newLicencePlate;

    @SerializedName("newSeatCount") @Expose
    public Integer newSeatCount;

    @SerializedName("newBabyFriendly") @Expose
    public Boolean newBabyFriendly;

    @SerializedName("newPetFriendly") @Expose
    public Boolean newPetFriendly;
}
