package com.komsiluk.taxi.data.remote.ride;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CancelRideDTO {

    @SerializedName("reason")
    @Expose
    private String reason;


    public CancelRideDTO() {}

    public String getReason() {
        return this.reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
