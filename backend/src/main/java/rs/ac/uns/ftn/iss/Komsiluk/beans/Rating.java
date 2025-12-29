package rs.ac.uns.ftn.iss.Komsiluk.beans;

import java.time.LocalDateTime;

public class Rating {

    private Long id;


    private Long rideId;

    private Long raterId;

    private Long driverId;
    private Long vehicleId;

    private Integer vehicleGrade;
    private Integer driverGrade;
    private String comment;
    private LocalDateTime createdAt;

    public Rating() { }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getRideId() { return rideId; }
    public void setRideId(Long rideId) { this.rideId = rideId; }

    public Long getRaterId() { return raterId; }
    public void setRaterId(Long raterId) { this.raterId = raterId; }

    public Long getDriverId() { return driverId; }
    public void setDriverId(Long driverId) { this.driverId = driverId; }

    public Long getVehicleId() { return vehicleId; }
    public void setVehicleId(Long vehicleId) { this.vehicleId = vehicleId; }

    public Integer getVehicleGrade() { return vehicleGrade; }
    public void setVehicleGrade(Integer vehicleGrade) { this.vehicleGrade = vehicleGrade; }

    public Integer getDriverGrade() { return driverGrade; }
    public void setDriverGrade(Integer driverGrade) { this.driverGrade = driverGrade; }

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
