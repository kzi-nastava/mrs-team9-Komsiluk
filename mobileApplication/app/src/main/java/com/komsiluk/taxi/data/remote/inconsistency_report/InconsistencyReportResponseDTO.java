package com.komsiluk.taxi.data.remote.inconsistency_report;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class InconsistencyReportResponseDTO {

    @SerializedName("id") @Expose
    private Long id;

    @SerializedName("rideId") @Expose
    private Long rideId;

    @SerializedName("reporterEmail")@Expose
    private String reporterEmail;
    @SerializedName("message") @Expose
    private String message;

    @SerializedName("createdAt") @Expose
    private String createdAt;

    // Getteri
    public Long getId() { return id; }
    public Long getRideId() { return rideId; }


    public String getMessage() { return message; }
    public String getCreatedAt() { return createdAt; }

    public String getReporterEmail() {
        return reporterEmail;
    }
}