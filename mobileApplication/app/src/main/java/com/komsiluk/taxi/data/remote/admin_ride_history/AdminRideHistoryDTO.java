package com.komsiluk.taxi.data.remote.admin_ride_history;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.komsiluk.taxi.data.remote.passenger_ride_history.CancellationSource;

import java.math.BigDecimal;

public class AdminRideHistoryDTO {

    @SerializedName("rideId")
    @Expose
    private Long rideId;

    @SerializedName("startAddress")
    @Expose
    private String startAddress;

    @SerializedName("endAddress")
    @Expose
    private String endAddress;

    @SerializedName("startTime")
    @Expose
    private String startTime;

    @SerializedName("endTime")
    @Expose
    private String endTime;

    @SerializedName("route")
    @Expose
    private String route;

    @SerializedName("panicTriggered")
    @Expose
    private boolean panicTriggered;

    @SerializedName("cancellationSource")
    @Expose
    private CancellationSource cancellationSource;

    @SerializedName("cancellationReason")
    @Expose
    private String cancellationReason;

    @SerializedName("price")
    @Expose
    private BigDecimal price;

    public AdminRideHistoryDTO() {
    }

    // Getters and Setters
    public Long getRideId() {
        return rideId;
    }

    public void setRideId(Long rideId) {
        this.rideId = rideId;
    }

    public String getStartAddress() {
        return startAddress;
    }

    public void setStartAddress(String startAddress) {
        this.startAddress = startAddress;
    }

    public String getEndAddress() {
        return endAddress;
    }

    public void setEndAddress(String endAddress) {
        this.endAddress = endAddress;
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

    public String getRoute() {
        return route;
    }

    public void setRoute(String route) {
        this.route = route;
    }

    public boolean isPanicTriggered() {
        return panicTriggered;
    }

    public void setPanicTriggered(boolean panicTriggered) {
        this.panicTriggered = panicTriggered;
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

    public boolean isCanceled() {
        return cancellationSource != null;
    }
}
