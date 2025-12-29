package rs.ac.uns.ftn.iss.Komsiluk.dtos.ride;

public class StopRideResponseDTO {
    private String finalAddress;
    private int durationMinutes;
    private double price;

    public StopRideResponseDTO() { }

    public String getFinalAddress() {
        return finalAddress;
    }
    public void setFinalAddress(String finalAddress) {
        this.finalAddress = finalAddress;
    }
    public int getDurationMinutes() {
        return durationMinutes;
    }
    public void setDurationMinutes(int durationMinutes) {
        this.durationMinutes = durationMinutes;
    }
    public double getPrice() {
        return price;
    }
    public void setPrice(double price) {
        this.price = price;
    }
}

