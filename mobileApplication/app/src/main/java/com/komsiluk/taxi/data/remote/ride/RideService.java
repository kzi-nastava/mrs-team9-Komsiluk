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

    @POST("rides/{id}/finish")
    Call<RideResponse> finishRide(@Path("id") Long rideId);

    @GET("rides/passenger/active")
    Call<RidePassengerActive> getPassengerActiveRide();

    @GET("rides/{rideId}")
    Call<AdminRideDetails> getRideDetails(@Path("rideId") Long rideId);

    @GET("rides/user/{userId}/scheduled")
    Call<List<RideResponse>> getScheduledRidesForUser(@Path("userId") Long userId);

    @POST("rides/{id}/panic")
    Call<Void> panic(@Path("id") Long rideId, @Body PanicRequestDTO panicDto);

    @GET("rides/active/all")
    Call<List<RideResponse>> getAllActiveRides();

    @POST("rides/{id}/cancel/driver")
    Call<Void> cancelByDriver(@Path("id") Long rideId, @Body CancelRideDTO cancelRideDTO);

    @POST("rides/{id}/cancel/passenger")
    Call<Void> cancelByPassenger(@Path("id") Long rideId, @Body CancelRideDTO cancelRideDTO);

    @POST("rides/{id}/stop")
    Call<RideResponse> stopRide(@Path("id") Long id, @Body StopRideRequestDTO body);
}
