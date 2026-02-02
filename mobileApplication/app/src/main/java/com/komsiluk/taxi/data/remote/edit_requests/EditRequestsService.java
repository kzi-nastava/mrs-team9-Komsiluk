package com.komsiluk.taxi.data.remote.edit_requests;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface EditRequestsService {

    @GET("/api/driver-edit-requests/pending")
    Call<List<ProfileChangeRequestResponse>> getPendingDriverEditRequests();

    @PUT("/api/driver-edit-requests/{requestId}/approve/{adminId}")
    Call<ProfileChangeRequestResponse> approveDriverEditRequest(
            @Path("requestId") Long requestId,
            @Path("adminId") Long adminId
    );

    @PUT("/api/driver-edit-requests/{requestId}/reject/{adminId}")
    Call<ProfileChangeRequestResponse> rejectDriverEditRequest(
            @Path("requestId") Long requestId,
            @Path("adminId") Long adminId
    );

}
