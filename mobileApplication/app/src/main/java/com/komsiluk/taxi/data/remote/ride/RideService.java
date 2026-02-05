package com.komsiluk.taxi.data.remote.ride;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface RideService {
    @POST("rides")
    Call<RideResponse> orderRide(@Body RideCreateRequest body);

    @GET("rides/user/{userId}/scheduled")
    Call<List<RideResponse>> getScheduledRides(@Path("userId") Long userId);

    @GET("rides/driver/{driverId}/current")
    Call<RideResponse> getDriverCurrentRide(@Path("driverId") Long driverId);

    @POST("/api/rides/{id}/start")
    Call<RideResponse> startRide(@Path("id") Long rideId);
}
