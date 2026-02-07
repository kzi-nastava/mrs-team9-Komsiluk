package com.komsiluk.taxi.data.remote.report;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.math.BigDecimal;
import java.util.List;

public class RideReportResponse {

    @Expose
    @SerializedName("ridesPerDay")
    private List<DailyValueResponse> ridesPerDay;

    @Expose
    @SerializedName("totalRides")
    private long totalRides;

    @Expose
    @SerializedName("averageRidesPerDay")
    private double averageRidesPerDay;

    @Expose
    @SerializedName("distancePerDay")
    private List<DailyValueResponse> distancePerDay;

    @Expose
    @SerializedName("totalDistanceKm")
    private double totalDistanceKm;

    @Expose
    @SerializedName("averageDistanceKmPerDay")
    private double averageDistanceKmPerDay;

    @Expose
    @SerializedName("moneyPerDay")
    private List<DailyValueResponse> moneyPerDay;

    @Expose
    @SerializedName("totalMoney")
    private BigDecimal totalMoney;

    @Expose
    @SerializedName("averageMoneyPerDay")
    private BigDecimal averageMoneyPerDay;

    public List<DailyValueResponse> getRidesPerDay() { return ridesPerDay; }
    public long getTotalRides() { return totalRides; }
    public double getAverageRidesPerDay() { return averageRidesPerDay; }

    public List<DailyValueResponse> getDistancePerDay() { return distancePerDay; }
    public double getTotalDistanceKm() { return totalDistanceKm; }
    public double getAverageDistanceKmPerDay() { return averageDistanceKmPerDay; }

    public List<DailyValueResponse> getMoneyPerDay() { return moneyPerDay; }
    public BigDecimal getTotalMoney() { return totalMoney; }
    public BigDecimal getAverageMoneyPerDay() { return averageMoneyPerDay; }
}
