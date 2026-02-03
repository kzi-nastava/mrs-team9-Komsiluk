package com.komsiluk.taxi.data.remote.driver_history;

import com.komsiluk.taxi.data.remote.rating.RatingResponseDTO;
import com.komsiluk.taxi.data.remote.ride.RideResponseDTO;
import com.komsiluk.taxi.data.remote.inconsistency_report.InconsistencyReportResponseDTO;

import java.util.Collection;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface DriverService {

    @GET("drivers/{id}/rides/history")
    Call<Collection<RideResponseDTO>> getDriverRideHistory(
            @Path("id") Long driverId,
            @Query("from") String from,
            @Query("to") String to
    );

    @GET("rides/{rideId}/ratings")
    Call<List<RatingResponseDTO>> getRideRatings(@Path("rideId") Long rideId);

    @GET("rides/{rideId}/inconsistencies")
    Call<List<InconsistencyReportResponseDTO>> getRideInconsistencies(@Path("rideId") Long rideId);
}