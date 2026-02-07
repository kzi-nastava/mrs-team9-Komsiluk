package com.komsiluk.taxi.data.remote.report;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ReportService {

    @GET("reports/users/{id}")
    Call<RideReportResponse> getUserReport(
            @Path("id") Long id,
            @Query("start") String startIso,  // yyyy-MM-dd
            @Query("end") String endIso
    );

    @GET("reports/drivers")
    Call<RideReportResponse> getAllDrivers(
            @Query("start") String startIso,
            @Query("end") String endIso
    );

    @GET("reports/passengers")
    Call<RideReportResponse> getAllPassengers(
            @Query("start") String startIso,
            @Query("end") String endIso
    );

    @GET("reports/users/by-email")
    Call<RideReportResponse> getUserByEmail(
            @Query("email") String email,
            @Query("start") String startIso,
            @Query("end") String endIso
    );
}
