package com.komsiluk.taxi.data.remote.passenger_ride_history;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.math.BigDecimal;
import java.util.List;

public class PassengerRideDetailsDTO {

    @SerializedName("rideId")
    @Expose
    private Long rideId;

    @SerializedName("status")
    @Expose
    private RideStatus status;

    @SerializedName("route")
    @Expose
    private RouteResponseDTO route;

    @SerializedName("createdAt")
    @Expose
    private String createdAt;

    @SerializedName("scheduledAt")
    @Expose
    private String scheduledAt;

    @SerializedName("startTime")
    @Expose
    private String startTime;

    @SerializedName("endTime")
    @Expose
    private String endTime;

    @SerializedName("driver")
    @Expose
    private DriverResponseDTO driver;

    @SerializedName("passengerIds")
    @Expose
    private List<Long> passengerIds;

    @SerializedName("passengerEmails")
    @Expose
    private List<String> passengerEmails;

    @SerializedName("creatorId")
    @Expose
    private Long creatorId;

    @SerializedName("creatorEmail")
    @Expose
    private String creatorEmail;

    @SerializedName("canceled")
    @Expose
    private boolean canceled;

    @SerializedName("cancellationSource")
    @Expose
    private CancellationSource cancellationSource;

    @SerializedName("cancellationReason")
    @Expose
    private String cancellationReason;

    @SerializedName("price")
    @Expose
    private BigDecimal price;

    @SerializedName("panicTriggered")
    @Expose
    private boolean panicTriggered;

    @SerializedName("vehicleType")
    @Expose
    private VehicleType vehicleType;

    @SerializedName("babyFriendly")
    @Expose
    private boolean babyFriendly;

    @SerializedName("petFriendly")
    @Expose
    private boolean petFriendly;

    @SerializedName("distanceKm")
    @Expose
    private double distanceKm;

    @SerializedName("estimatedDurationMin")
    @Expose
    private int estimatedDurationMin;

    @SerializedName("ratings")
    @Expose
    private List<RatingResponseDTO> ratings;

    @SerializedName("inconsistencyReports")
    @Expose
    private List<InconsistencyReportResponseDTO> inconsistencyReports;

    public PassengerRideDetailsDTO() {
    }

    // Getters and Setters
    public Long getRideId() {
        return rideId;
    }

    public void setRideId(Long rideId) {
        this.rideId = rideId;
    }

    public RideStatus getStatus() {
        return status;
    }

    public void setStatus(RideStatus status) {
        this.status = status;
    }

    public RouteResponseDTO getRoute() {
        return route;
    }

    public void setRoute(RouteResponseDTO route) {
        this.route = route;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getScheduledAt() {
        return scheduledAt;
    }

    public void setScheduledAt(String scheduledAt) {
        this.scheduledAt = scheduledAt;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public DriverResponseDTO getDriver() {
        return driver;
    }

    public void setDriver(DriverResponseDTO driver) {
        this.driver = driver;
    }

    public List<Long> getPassengerIds() {
        return passengerIds;
    }

    public void setPassengerIds(List<Long> passengerIds) {
        this.passengerIds = passengerIds;
    }

    public List<String> getPassengerEmails() {
        return passengerEmails;
    }

    public void setPassengerEmails(List<String> passengerEmails) {
        this.passengerEmails = passengerEmails;
    }

    public Long getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(Long creatorId) {
        this.creatorId = creatorId;
    }

    public String getCreatorEmail() {
        return creatorEmail;
    }

    public void setCreatorEmail(String creatorEmail) {
        this.creatorEmail = creatorEmail;
    }

    public boolean isCanceled() {
        return canceled;
    }

    public void setCanceled(boolean canceled) {
        this.canceled = canceled;
    }

    public CancellationSource getCancellationSource() {
        return cancellationSource;
    }

    public void setCancellationSource(CancellationSource cancellationSource) {
        this.cancellationSource = cancellationSource;
    }

    public String getCancellationReason() {
        return cancellationReason;
    }

    public void setCancellationReason(String cancellationReason) {
        this.cancellationReason = cancellationReason;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public boolean isPanicTriggered() {
        return panicTriggered;
    }

    public void setPanicTriggered(boolean panicTriggered) {
        this.panicTriggered = panicTriggered;
    }

    public VehicleType getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(VehicleType vehicleType) {
        this.vehicleType = vehicleType;
    }

    public boolean isBabyFriendly() {
        return babyFriendly;
    }

    public void setBabyFriendly(boolean babyFriendly) {
        this.babyFriendly = babyFriendly;
    }

    public boolean isPetFriendly() {
        return petFriendly;
    }

    public void setPetFriendly(boolean petFriendly) {
        this.petFriendly = petFriendly;
    }

    public double getDistanceKm() {
        return distanceKm;
    }

    public void setDistanceKm(double distanceKm) {
        this.distanceKm = distanceKm;
    }

    public int getEstimatedDurationMin() {
        return estimatedDurationMin;
    }

    public void setEstimatedDurationMin(int estimatedDurationMin) {
        this.estimatedDurationMin = estimatedDurationMin;
    }

    public List<RatingResponseDTO> getRatings() {
        return ratings;
    }

    public void setRatings(List<RatingResponseDTO> ratings) {
        this.ratings = ratings;
    }

    public List<InconsistencyReportResponseDTO> getInconsistencyReports() {
        return inconsistencyReports;
    }

    public void setInconsistencyReports(List<InconsistencyReportResponseDTO> inconsistencyReports) {
        this.inconsistencyReports = inconsistencyReports;
    }
}
