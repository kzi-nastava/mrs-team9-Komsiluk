package com.komsiluk.taxi.data.remote.passenger_ride_history;

import com.google.gson.annotations.SerializedName;

public enum CancellationSource {
    @SerializedName("DRIVER")
    DRIVER,
    @SerializedName("PASSENGER")
    PASSENGER
}
