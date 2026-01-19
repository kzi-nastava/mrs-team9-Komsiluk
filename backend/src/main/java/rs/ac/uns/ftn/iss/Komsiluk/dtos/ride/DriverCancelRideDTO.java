package rs.ac.uns.ftn.iss.Komsiluk.dtos.ride;

import jakarta.validation.constraints.NotBlank;

public class DriverCancelRideDTO {

    @NotBlank(message = "Cancellation reason is required")
    private String reason;

    public DriverCancelRideDTO() { }

    public String getReason() {
        return reason;
    }
    public void setReason(String reason) {
        this.reason = reason;
    }
}

