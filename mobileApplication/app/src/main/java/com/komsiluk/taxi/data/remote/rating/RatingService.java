package com.komsiluk.taxi.data.remote.rating;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface RatingService {
    @POST("rides/{rideId}/ratings")
    Call<ResponseBody> createRating(
            @Path("rideId") Long rideId,
            @Body RatingCreate dto
    );

    @GET("rides/{rideId}/ratings")
    Call<List<RatingResponse>> getRideRatings(@Path("rideId") Long rideId);
}