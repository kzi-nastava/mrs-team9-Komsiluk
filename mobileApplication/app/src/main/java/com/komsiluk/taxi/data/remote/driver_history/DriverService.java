package com.komsiluk.taxi.data.remote.driver_history;

import com.komsiluk.taxi.data.remote.add_driver.DriverResponse;
import com.komsiluk.taxi.data.remote.change_driver_status.DriverStatusUpdate;
import com.komsiluk.taxi.data.remote.rating.RatingResponse;
import com.komsiluk.taxi.data.remote.ride.RideResponse;
import com.komsiluk.taxi.data.remote.inconsistency_report.InconsistencyReportResponse;

import java.util.Collection;
import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface DriverService {

    @GET("drivers/{id}/rides/history")
    Call<Collection<RideResponse>> getDriverRideHistory(
            @Path("id") Long driverId,
            @Query("from") String from,
            @Query("to") String to
    );

    @GET("rides/{rideId}/ratings")
    Call<List<RatingResponse>> getRideRatings(@Path("rideId") Long rideId);

    @GET("rides/{rideId}/inconsistencies")
    Call<List<InconsistencyReportResponse>> getRideInconsistencies(@Path("rideId") Long rideId);

    @Multipart
    @POST("drivers")
    Call<DriverResponse> registerDriver(
            @Part("data") RequestBody data,
            @Part MultipartBody.Part profileImage
    );

    @PUT("drivers/{id}/status")
    Call<DriverResponse> changeDriverStatus(
            @Path("id") Long userId,
            @Body DriverStatusUpdate statusUpdate
    );
}