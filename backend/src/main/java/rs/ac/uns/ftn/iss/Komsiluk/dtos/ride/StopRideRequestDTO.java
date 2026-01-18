package rs.ac.uns.ftn.iss.Komsiluk.dtos.ride;

//import org.antlr.v4.runtime.misc.NotNull;
//import jakarta.validation.constraints.NotBlank;
//import jakarta.validation.constraints.NotNull;
//import jakarta.validation.constraints.Positive;


public class StopRideRequestDTO {

    private String stopAddress;

    private String visitedStops;


    private double distanceTravelledKm;

    private int durationMinutes;

    public StopRideRequestDTO() { }

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

    public int getDurationMinutes() {
        return durationMinutes;
    }
    public void setDurationMinutes(int durationMinutes) {
        this.durationMinutes = durationMinutes;
    }
}
