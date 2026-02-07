package com.komsiluk.taxi.data.remote.rating;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RatingResponse {

    @SerializedName("id") @Expose
    private Long id;

    @SerializedName("raterMail") @Expose
    private String raterMail;

    @SerializedName("driverGrade") @Expose
    private Integer driverGrade;

    @SerializedName("vehicleGrade") @Expose
    private Integer vehicleGrade;

    @SerializedName("comment") @Expose
    private String comment;

    // Getteri
    public Long getId() { return id; }
    public String getRaterMail() { return raterMail; }
    public Integer getDriverGrade() { return driverGrade; }
    public Integer getVehicleGrade() { return vehicleGrade; }
    public String getComment() { return comment; }
}