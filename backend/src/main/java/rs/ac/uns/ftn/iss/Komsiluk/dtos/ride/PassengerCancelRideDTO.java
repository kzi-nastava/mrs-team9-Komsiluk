package rs.ac.uns.ftn.iss.Komsiluk.dtos.ride;

import jakarta.validation.constraints.NotBlank;

public class PassengerCancelRideDTO {

    @NotBlank(message = "Cancellation reason is required")
    private String reason;

    public PassengerCancelRideDTO() { }
    public String getReason() {
        return reason;
    }
    public void setReason(String reason) {
        this.reason = reason;
    }
}

