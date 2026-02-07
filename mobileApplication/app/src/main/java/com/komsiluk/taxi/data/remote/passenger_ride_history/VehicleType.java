package com.komsiluk.taxi.data.remote.passenger_ride_history;

import com.google.gson.annotations.SerializedName;

public enum VehicleType {
    @SerializedName("STANDARD")
    STANDARD,
    @SerializedName("LUXURY")
    LUXURY,
    @SerializedName("VAN")
    VAN
}
