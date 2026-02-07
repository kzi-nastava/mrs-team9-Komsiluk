package com.komsiluk.taxi.data.remote.passenger_ride_history;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RatingResponseDTO {

    @SerializedName("id")
    @Expose
    private Long id;

    @SerializedName("rideId")
    @Expose
    private Long rideId;

    @SerializedName("raterId")
    @Expose
    private Long raterId;

    @SerializedName("raterMail")
    @Expose
    private String raterMail;

    @SerializedName("driverId")
    @Expose
    private Long driverId;

    @SerializedName("vehicleId")
    @Expose
    private Long vehicleId;

    @SerializedName("vehicleGrade")
    @Expose
    private Integer vehicleGrade;

    @SerializedName("driverGrade")
    @Expose
    private Integer driverGrade;

    @SerializedName("comment")
    @Expose
    private String comment;

    @SerializedName("createdAt")
    @Expose
    private String createdAt;

    public RatingResponseDTO() {
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

    public Long getRaterId() {
        return raterId;
    }

    public void setRaterId(Long raterId) {
        this.raterId = raterId;
    }

    public String getRaterMail() {
        return raterMail;
    }

    public void setRaterMail(String raterMail) {
        this.raterMail = raterMail;
    }

    public Long getDriverId() {
        return driverId;
    }

    public void setDriverId(Long driverId) {
        this.driverId = driverId;
    }

    public Long getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(Long vehicleId) {
        this.vehicleId = vehicleId;
    }

    public Integer getVehicleGrade() {
        return vehicleGrade;
    }

    public void setVehicleGrade(Integer vehicleGrade) {
        this.vehicleGrade = vehicleGrade;
    }

    public Integer getDriverGrade() {
        return driverGrade;
    }

    public void setDriverGrade(Integer driverGrade) {
        this.driverGrade = driverGrade;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}
