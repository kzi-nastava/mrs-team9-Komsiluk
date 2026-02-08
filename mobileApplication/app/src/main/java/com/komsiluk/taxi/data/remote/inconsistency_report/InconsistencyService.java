package com.komsiluk.taxi.data.remote.inconsistency_report;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface InconsistencyService {
    // U RideService.java ili novom InconsistencyService.java
    @POST("rides/{rideId}/inconsistencies")
    Call<ResponseBody> reportInconsistency(
            @Path("rideId") Long rideId,
            @Body InconsistencyReportCreate dto
    );

    @GET("rides/{rideId}/inconsistencies")
    Call<List<InconsistencyReportResponse>> getRideInconsistencies(@Path("rideId") Long rideId);
}
