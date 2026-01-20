package rs.ac.uns.ftn.iss.Komsiluk.beans;

import java.time.LocalDateTime;
import jakarta.persistence.*;

@Entity
@Table(
        name = "ratings",
        uniqueConstraints = @UniqueConstraint(name = "uk_rating_ride_rater", columnNames = {"ride_id", "rater_id"})
)
public class Rating {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="ride_id", nullable=false)
    private Long rideId;

    @Column(name="rater_id", nullable=false)
    private Long raterId;

    @Column(name="driver_id")
    private Long driverId;

    @Column(name="vehicle_id")
    private Long vehicleId;

    @Column(name="vehicle_grade", nullable=false)
    private Integer vehicleGrade;

    @Column(name="driver_grade", nullable=false)
    private Integer driverGrade;

    @Column(name="comment")
    private String comment;

    @Column(name="created_at", nullable=false)
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
