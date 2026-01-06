package rs.ac.uns.ftn.iss.Komsiluk.dtos.ride;

public class RideEstimateResponseDTO {
    private String startAddress;
    private String destinationAddress;
    private double distanceKm;
    private int estimatedDurationMin;

    public RideEstimateResponseDTO() { }

    public String getStartAddress() {
        return startAddress;
    }
    public void setStartAddress(String startAddress) {
        this.startAddress = startAddress;
    }
    public String getDestinationAddress() {
        return destinationAddress;
    }
    public void setDestinationAddress(String endAddress) {
        this.destinationAddress = endAddress;
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
}

