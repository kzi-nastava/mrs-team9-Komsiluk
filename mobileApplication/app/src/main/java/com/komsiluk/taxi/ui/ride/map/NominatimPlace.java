package com.komsiluk.taxi.ui.ride.map;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class NominatimPlace {
    @SerializedName("display_name") @Expose public String displayName;
    @SerializedName("lat") @Expose public String lat;
    @SerializedName("lon") @Expose public String lon;
}
