package rs.ac.uns.ftn.iss.Komsiluk.dtos.ride;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.util.List;

public class StopRideRequestDTO {

    @NotBlank(message = "Stop address is required")
    private String stopAddress;

    @Size(max = 10)
    private List<@NotBlank @Size(min = 2, max = 150) String> visitedStops;

    @Positive(message = "Travelled distance must be greater than zero")
    private double distanceTravelledKm;


    public StopRideRequestDTO() {}

    public String getStopAddress() {
        return stopAddress;
    }

    public void setStopAddress(String stopAddress) {
        this.stopAddress = stopAddress;
    }

    public List<String> getVisitedStops() {
        return visitedStops;
    }

    public void setVisitedStops(List<String> stops) {
        this.visitedStops = stops;
    }

    public double getDistanceTravelledKm() {
        return distanceTravelledKm;
    }

    public void setDistanceTravelledKm(double distanceTravelledKm) {
        this.distanceTravelledKm = distanceTravelledKm;
    }

}
