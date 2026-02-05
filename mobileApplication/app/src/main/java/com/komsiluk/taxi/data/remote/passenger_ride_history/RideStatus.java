package com.komsiluk.taxi.data.remote.passenger_ride_history;

import com.google.gson.annotations.SerializedName;

public enum RideStatus {
    @SerializedName("PENDING")
    PENDING,
    @SerializedName("ACCEPTED")
    ACCEPTED,
    @SerializedName("IN_PROGRESS")
    IN_PROGRESS,
    @SerializedName("COMPLETED")
    COMPLETED,
    @SerializedName("CANCELLED")
    CANCELLED,
    @SerializedName("REJECTED")
    REJECTED
}
