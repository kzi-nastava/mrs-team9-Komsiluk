package rs.ac.uns.ftn.iss.Komsiluk.dtos.map;

import rs.ac.uns.ftn.iss.Komsiluk.dtos.vehicle.VehicleResponseDTO;

public class ActiveVehicleOnMapDTO {

    private Long driverId;
    private boolean busy;
    private double latitude;
    private double longitude;

    private VehicleResponseDTO vehicle;

    public ActiveVehicleOnMapDTO() {}

    public Long getDriverId() {
        return driverId;
    }

    public void setDriverId(Long driverId) {
        this.driverId = driverId;
    }

    public boolean isBusy() {
        return busy;
    }

    public void setBusy(boolean busy) {
        this.busy = busy;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public VehicleResponseDTO getVehicle() {
        return vehicle;
    }

    public void setVehicle(VehicleResponseDTO vehicle) {
        this.vehicle = vehicle;
    }
}
