package rs.ac.uns.ftn.iss.Komsiluk.dtos.report;

import java.math.BigDecimal;
import java.util.List;

public class RideReportDTO {

    private List<DailyValueDTO> ridesPerDay;
    private long totalRides;
    private double averageRidesPerDay;

    private List<DailyValueDTO> distancePerDay;
    private double totalDistanceKm;
    private double averageDistanceKmPerDay;

    private List<DailyValueDTO> moneyPerDay;
    private BigDecimal totalMoney;
    private BigDecimal averageMoneyPerDay;

    public RideReportDTO() {
    }

    public List<DailyValueDTO> getRidesPerDay() {
        return ridesPerDay;
    }

    public void setRidesPerDay(List<DailyValueDTO> ridesPerDay) {
        this.ridesPerDay = ridesPerDay;
    }

    public long getTotalRides() {
        return totalRides;
    }

    public void setTotalRides(long totalRides) {
        this.totalRides = totalRides;
    }

    public double getAverageRidesPerDay() {
        return averageRidesPerDay;
    }

    public void setAverageRidesPerDay(double averageRidesPerDay) {
        this.averageRidesPerDay = averageRidesPerDay;
    }

    public List<DailyValueDTO> getDistancePerDay() {
        return distancePerDay;
    }

    public void setDistancePerDay(List<DailyValueDTO> distancePerDay) {
        this.distancePerDay = distancePerDay;
    }

    public double getTotalDistanceKm() {
        return totalDistanceKm;
    }

    public void setTotalDistanceKm(double totalDistanceKm) {
        this.totalDistanceKm = totalDistanceKm;
    }

    public double getAverageDistanceKmPerDay() {
        return averageDistanceKmPerDay;
    }

    public void setAverageDistanceKmPerDay(double averageDistanceKmPerDay) {
        this.averageDistanceKmPerDay = averageDistanceKmPerDay;
    }

    public List<DailyValueDTO> getMoneyPerDay() {
        return moneyPerDay;
    }

    public void setMoneyPerDay(List<DailyValueDTO> moneyPerDay) {
        this.moneyPerDay = moneyPerDay;
    }

    public BigDecimal getTotalMoney() {
        return totalMoney;
    }

    public void setTotalMoney(BigDecimal totalMoney) {
        this.totalMoney = totalMoney;
    }

    public BigDecimal getAverageMoneyPerDay() {
        return averageMoneyPerDay;
    }

    public void setAverageMoneyPerDay(BigDecimal averageMoneyPerDay) {
        this.averageMoneyPerDay = averageMoneyPerDay;
    }
}