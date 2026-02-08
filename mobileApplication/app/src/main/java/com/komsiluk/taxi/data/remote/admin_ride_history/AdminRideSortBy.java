package com.komsiluk.taxi.data.remote.admin_ride_history;

import com.google.gson.annotations.SerializedName;

public enum AdminRideSortBy {
    @SerializedName("DATE")
    DATE,
    @SerializedName("START_TIME")
    START_TIME,
    @SerializedName("END_TIME")
    END_TIME,
    @SerializedName("ROUTE")
    ROUTE,
    @SerializedName("START_ADDRESS")
    START_ADDRESS,
    @SerializedName("END_ADDRESS")
    END_ADDRESS,
    @SerializedName("PANIC")
    PANIC,
    @SerializedName("CANCELLED")
    CANCELED,
    @SerializedName("PRICE")
    PRICE
}
