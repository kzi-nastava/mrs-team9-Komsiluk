package rs.ac.uns.ftn.iss.Komsiluk.dtos.ride;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

public class StopRideRequestDTO {

    @NotBlank(message = "Stop address is required")
    private String stopAddress;

    private String visitedStops;

    @Positive(message = "Travelled distance must be greater than zero")
    private double distanceTravelledKm;


    public StopRideRequestDTO() {}

    public String getStopAddress() {
        return stopAddress;
    }

    public void setStopAddress(String stopAddress) {
        this.stopAddress = stopAddress;
    }

    public String getVisitedStops() {
        return visitedStops;
    }

    public void setVisitedStops(String visitedStops) {
        this.visitedStops = visitedStops;
    }

    public double getDistanceTravelledKm() {
        return distanceTravelledKm;
    }

    public void setDistanceTravelledKm(double distanceTravelledKm) {
        this.distanceTravelledKm = distanceTravelledKm;
    }

}
