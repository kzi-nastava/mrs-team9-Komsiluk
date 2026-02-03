package com.komsiluk.taxi.data.remote.ride;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class RideResponseDTO {

    @SerializedName("id") @Expose
    private Long id;

    @SerializedName("status") @Expose
    private String status;

    @SerializedName("startTime") @Expose
    private String startTime;

    @SerializedName("endTime") @Expose
    private String endTime;

    @SerializedName("price") @Expose
    private Double price;

    @SerializedName("startAddress") @Expose
    private String startAddress;

    @SerializedName("endAddress") @Expose
    private String endAddress;

    @SerializedName("stops") @Expose
    private List<String> stops; // Na backu je malo 's', ovde je bilo 'S'

    @SerializedName("passengerIds") @Expose
    private List<Long> passengerIds;

    @SerializedName("passengerEmails") @Expose
    private List<String> passengerEmails; // Ovo je falilo za listu putnika

    @SerializedName("distanceKm") @Expose
    private Double distanceKm;

    @SerializedName("estimatedDurationMin") @Expose
    private Integer estimatedDurationMin;

    @SerializedName("panicTriggered") @Expose
    private boolean panicTriggered;

    // Getteri
    public Long getId() { return id; }
    public String getStatus() { return status; }
    public String getStartTime() { return startTime; }
    public String getEndTime() { return endTime; }
    public Double getPrice() { return price; }
    public String getStartAddress() { return startAddress; }
    public String getEndAddress() { return endAddress; }
    public List<String> getStops() { return stops; }
    public List<Long> getPassengerIds() { return passengerIds; }
    public List<String> getPassengerEmails() { return passengerEmails; }
    public Double getDistanceKm() { return distanceKm; }
    public Integer getEstimatedDurationMin() { return estimatedDurationMin; }
    public boolean isPanicTriggered() { return panicTriggered; }
}