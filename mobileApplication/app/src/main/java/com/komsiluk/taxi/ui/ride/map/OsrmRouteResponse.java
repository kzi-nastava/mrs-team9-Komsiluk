package com.komsiluk.taxi.ui.ride.map;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class OsrmRouteResponse {

    @SerializedName("routes") @Expose public List<Route> routes;

    public static class Route {
        @SerializedName("distance") @Expose public double distance;
        @SerializedName("duration") @Expose public double duration;
        @SerializedName("geometry") @Expose public Geometry geometry;
    }

    public static class Geometry {
        @SerializedName("coordinates") @Expose public List<List<Double>> coordinates;
    }
}
