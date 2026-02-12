package rs.ac.uns.ftn.iss.Komsiluk.dtos.ride;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class PassengerCancelRideDTO {

    @NotBlank(message = "Cancellation reason is required")
    @Size(max = 200, message = "Cancellation reason must not exceed 200 characters")
    private String reason;

    public PassengerCancelRideDTO() { }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}

