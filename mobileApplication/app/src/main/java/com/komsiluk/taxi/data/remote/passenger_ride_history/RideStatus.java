package com.komsiluk.taxi.data.remote.passenger_ride_history;

import com.google.gson.annotations.SerializedName;

public enum RideStatus {
    @SerializedName("REQUESTED")
    REQUESTED,
    @SerializedName("ASSIGNED")
    ASSIGNED,
    @SerializedName("ACTIVE")
    ACTIVE,
    @SerializedName("FINISHED")
    FINISHED,
    @SerializedName("CANCELLED")
    CANCELLED,
    @SerializedName("REJECTED")
    REJECTED,
    @SerializedName("SCHEDULED")
    SCHEDULED
}
