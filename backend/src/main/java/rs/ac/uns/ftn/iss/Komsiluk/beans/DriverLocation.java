package rs.ac.uns.ftn.iss.Komsiluk.beans;

import java.time.LocalDateTime;

public class DriverLocation {

    private Long driverId;
    private double lat;
    private double lng;
    private LocalDateTime updatedAt;

    public DriverLocation() {
        super();
    }

    public DriverLocation(Long driverId, double lat, double lng, LocalDateTime updatedAt) {
        super();
        this.driverId = driverId;
        this.lat = lat;
        this.lng = lng;
        this.updatedAt = updatedAt;
    }

    public Long getDriverId() {
        return driverId;
    }

    public void setDriverId(Long driverId) {
        this.driverId = driverId;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
