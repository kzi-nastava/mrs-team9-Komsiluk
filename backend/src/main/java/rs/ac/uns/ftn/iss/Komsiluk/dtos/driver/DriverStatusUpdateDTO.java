package rs.ac.uns.ftn.iss.Komsiluk.dtos.driver;

import rs.ac.uns.ftn.iss.Komsiluk.beans.enums.DriverStatus;

public class DriverStatusUpdateDTO {

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