package com.komsiluk.taxi.data.remote.price;

import com.google.gson.annotations.SerializedName;

public class PriceUpdate {
    @SerializedName("startingPrice")
    public Integer startingPrice;
    @SerializedName("pricePerKm")
    public Integer pricePerKm;

    public PriceUpdate(Integer startingPrice, Integer pricePerKm) {
        this.startingPrice = startingPrice;
        this.pricePerKm = pricePerKm;
    }
}