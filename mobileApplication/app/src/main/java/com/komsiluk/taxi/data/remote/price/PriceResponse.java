package com.komsiluk.taxi.data.remote.price;

import com.google.gson.annotations.SerializedName;

public class PriceResponse {
    @SerializedName("vehicleType")
    public String vehicleType;
    @SerializedName("startingPrice")
    public Integer startingPrice;
    @SerializedName("pricePerKm")
    public Integer pricePerKm;

    public String getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
    }

    public Integer getStartingPrice() {
        return startingPrice;
    }

    public void setStartingPrice(Integer startingPrice) {
        this.startingPrice = startingPrice;
    }

    public Integer getPricePerKm() {
        return pricePerKm;
    }

    public void setPricePerKm(Integer pricePerKm) {
        this.pricePerKm = pricePerKm;
    }
}