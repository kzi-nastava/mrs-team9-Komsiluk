package rs.ac.uns.ftn.iss.Komsiluk.beans;

import java.time.LocalDateTime;

public class InconsistencyReport {

    private Long id;
    private Long rideId;
    private Long passengerId;
    private String message;
    private LocalDateTime createdAt;

    public InconsistencyReport() {
        super();
    }

    public InconsistencyReport(Long id, Long rideId, Long passengerId, String message, LocalDateTime createdAt) {
        super();
        this.id = id;
        this.rideId = rideId;
        this.passengerId = passengerId;
        this.message = message;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getRideId() {
        return rideId;
    }

    public void setRideId(Long rideId) {
        this.rideId = rideId;
    }

    public Long getPassengerId() {
        return passengerId;
    }

    public void setPassengerId(Long passengerId) {
        this.passengerId = passengerId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
