package com.komsiluk.taxi.data.remote.passenger_ride_history;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface PassengerRideHistoryService {

    @Headers({
            "User-Agent: Mobile-Android",
            "Content-Type: application/json"
    })
    @GET("passengers/{userId}/rides")
    Call<List<PassengerRideHistoryDTO>> getPassengerRides(
            @Path("userId") Long userId,
            @Query("from") String fromDate,
            @Query("to") String toDate,
            @Query("sortBy") PassengerRideSortBy sortBy
    );

    @Headers({
            "User-Agent: Mobile-Android",
            "Content-Type: application/json"
    })
    @GET("rides/{rideId}")
    Call<PassengerRideDetailsDTO> getRideDetails(
            @Path("rideId") Long rideId
    );
}
