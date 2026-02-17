package com.komsiluk.taxi.data.remote.passenger_ride_history;

import com.google.gson.annotations.SerializedName;

public enum PassengerRideSortBy {
    @SerializedName("ROUTE")
    ROUTE,
    @SerializedName("START_TIME")
    START_TIME,
    @SerializedName("END_TIME")
    END_TIME
}
