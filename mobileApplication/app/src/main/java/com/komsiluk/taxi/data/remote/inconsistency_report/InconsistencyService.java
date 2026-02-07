package com.komsiluk.taxi.data.remote.inconsistency_report;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface InconsistencyService {
    // U RideService.java ili novom InconsistencyService.java
    @POST("rides/{rideId}/inconsistencies")
    Call<ResponseBody> reportInconsistency(
            @Path("rideId") Long rideId,
            @Body InconsistencyReportCreate dto
    );
}
