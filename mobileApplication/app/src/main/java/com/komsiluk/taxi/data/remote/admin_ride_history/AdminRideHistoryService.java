package com.komsiluk.taxi.data.remote.admin_ride_history;

import com.komsiluk.taxi.data.remote.passenger_ride_history.PassengerRideDetailsDTO;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface AdminRideHistoryService {

    @Headers({
            "User-Agent: Mobile-Android",
            "Content-Type: application/json"
    })
    @GET("admin/rides/by-user-email")
    Call<List<AdminRideHistoryDTO>> getRidesByUserEmail(
            @Query("email") String email,
            @Query("from") String fromDate,
            @Query("to") String toDate,
            @Query("sortBy") AdminRideSortBy sortBy
    );

    @Headers({
            "User-Agent: Mobile-Android",
            "Content-Type: application/json"
    })
    @GET("rides/{rideId}")
    Call<PassengerRideDetailsDTO> getRideDetails(@Path("rideId") Long rideId);
}
