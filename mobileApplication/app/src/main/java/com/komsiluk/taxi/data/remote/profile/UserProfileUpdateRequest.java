package com.komsiluk.taxi.data.remote.profile;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UserProfileUpdateRequest {

    @SerializedName("firstName") @Expose
    private String firstName;

    @SerializedName("lastName") @Expose
    private String lastName;

    @SerializedName("address") @Expose
    private String address;

    @SerializedName("city") @Expose
    private String city;

    @SerializedName("phoneNumber") @Expose
    private String phoneNumber;

    @SerializedName("profileImageUrl") @Expose
    private String profileImageUrl;

    public UserProfileUpdateRequest(
            String firstName,
            String lastName,
            String address,
            String city,
            String phoneNumber
    ) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.address = address;
        this.city = city;
        this.phoneNumber = phoneNumber;
        this.profileImageUrl = null;
    }
}
