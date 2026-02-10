package com.komsiluk.taxi.data.remote.change_driver_status;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DriverStatusUpdate {

    @SerializedName("status")
    @Expose
    private DriverStatus status;

    public DriverStatusUpdate() {
    }

    public DriverStatusUpdate(DriverStatus status) {
        this.status = status;
    }

    public DriverStatus getStatus() {
        return status;
    }

    public void setStatus(DriverStatus status) {
        this.status = status;
    }
}
