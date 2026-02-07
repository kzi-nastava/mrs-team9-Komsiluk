package com.komsiluk.taxi.data.remote.passenger_ride_history;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.komsiluk.taxi.auth.UserRole;

public class InconsistencyReportResponseDTO {

    @SerializedName("id")
    @Expose
    private Long id;

    @SerializedName("rideId")
    @Expose
    private Long rideId;

    @SerializedName("reporterId")
    @Expose
    private Long reporterId;

    @SerializedName("reporterRole")
    @Expose
    private UserRole reporterRole;

    @SerializedName("reporterEmail")
    @Expose
    private String reporterEmail;

    @SerializedName("message")
    @Expose
    private String message;

    @SerializedName("createdAt")
    @Expose
    private String createdAt;

    public InconsistencyReportResponseDTO() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getRideId() {
        return rideId;
    }

    public void setRideId(Long rideId) {
        this.rideId = rideId;
    }

    public Long getReporterId() {
        return reporterId;
    }

    public void setReporterId(Long reporterId) {
        this.reporterId = reporterId;
    }

    public UserRole getReporterRole() {
        return reporterRole;
    }

    public void setReporterRole(UserRole reporterRole) {
        this.reporterRole = reporterRole;
    }

    public String getReporterEmail() {
        return reporterEmail;
    }

    public void setReporterEmail(String reporterEmail) {
        this.reporterEmail = reporterEmail;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}
