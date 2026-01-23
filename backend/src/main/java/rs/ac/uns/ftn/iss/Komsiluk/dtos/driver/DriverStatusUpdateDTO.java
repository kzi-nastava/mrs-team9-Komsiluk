package rs.ac.uns.ftn.iss.Komsiluk.dtos.driver;

import jakarta.validation.constraints.NotNull;
import rs.ac.uns.ftn.iss.Komsiluk.beans.enums.DriverStatus;

public class DriverStatusUpdateDTO {

	@NotNull
    private DriverStatus status;

    public DriverStatusUpdateDTO() {
    }

    public DriverStatus getStatus() {
        return status;
    }

    public void setStatus(DriverStatus status) {
        this.status = status;
    }
}