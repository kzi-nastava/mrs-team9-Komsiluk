package com.komsiluk.taxi.ui.ride.map;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface NominatimService {
    @GET("search")
    Call<List<NominatimPlace>> search(
            @Query("q") String q,
            @Query("format") String format,
            @Query("addressdetails") int addressDetails,
            @Query("limit") int limit,
            @Query("countrycodes") String countrycodes,
            @Query("bounded") int bounded,
            @Query("viewbox") String viewbox
    );
}
