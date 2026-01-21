package rs.ac.uns.ftn.iss.Komsiluk.dtos.location;

import java.time.LocalDateTime;

public class DriverLocationResponseDTO {

    private Long driverId;
    private double lat;
    private double lng;
    private LocalDateTime updatedAt;
    private boolean busy;

    public DriverLocationResponseDTO() {
        super();
    }
    public boolean isBusy() { return busy; }
    public void setBusy(boolean busy) { this.busy = busy; }
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
