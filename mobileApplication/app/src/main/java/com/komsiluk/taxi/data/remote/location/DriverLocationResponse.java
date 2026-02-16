package com.komsiluk.taxi.data.remote.location;

public class DriverLocationResponse {
    private Long driverId;
    private double lat;
    private double lng;

    private String updatedAt;
    private boolean busy;

    private boolean panic;

    public Long getDriverId() { return driverId; }
    public double getLat() { return lat; }
    public double getLng() { return lng; }
    public boolean isBusy() { return busy; }

    public String getUpdatedAt(){return  updatedAt;}

    public boolean isPanic() {
        return this.panic;
    }
}
