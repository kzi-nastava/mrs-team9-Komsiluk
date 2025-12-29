package rs.ac.uns.ftn.iss.Komsiluk.dtos.ride;

import java.time.LocalDateTime;

import rs.ac.uns.ftn.iss.Komsiluk.beans.enums.RideStatus;

public class RideLiveInfoDTO {

    private Long rideId;
    private RideStatus status;

    private Long driverId;


    private Double lat;
    private Double lng;
    private LocalDateTime locationUpdatedAt;

    private Integer remainingMinutes;

    public RideLiveInfoDTO() {
        super();
    }

    public Long getRideId() { return rideId; }
    public void setRideId(Long rideId) { this.rideId = rideId; }

    public RideStatus getStatus() { return status; }
    public void setStatus(RideStatus status) { this.status = status; }

    public Long getDriverId() { return driverId; }
    public void setDriverId(Long driverId) { this.driverId = driverId; }

    public Double getLat() { return lat; }
    public void setLat(Double lat) { this.lat = lat; }

    public Double getLng() { return lng; }
    public void setLng(Double lng) { this.lng = lng; }

    public LocalDateTime getLocationUpdatedAt() { return locationUpdatedAt; }
    public void setLocationUpdatedAt(LocalDateTime locationUpdatedAt) { this.locationUpdatedAt = locationUpdatedAt; }

    public Integer getRemainingMinutes() { return remainingMinutes; }
    public void setRemainingMinutes(Integer remainingMinutes) { this.remainingMinutes = remainingMinutes; }
}
