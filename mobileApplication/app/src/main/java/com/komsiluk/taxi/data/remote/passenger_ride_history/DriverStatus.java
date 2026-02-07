package com.komsiluk.taxi.data.remote.passenger_ride_history;

import com.google.gson.annotations.SerializedName;

public enum DriverStatus {
    @SerializedName("ACTIVE")
    ACTIVE,
    @SerializedName("INACTIVE")
    INACTIVE,
    @SerializedName("IN_RIDE")
    IN_RIDE
}
