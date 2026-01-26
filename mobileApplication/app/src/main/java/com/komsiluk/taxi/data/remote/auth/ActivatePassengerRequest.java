package com.komsiluk.taxi.data.remote.auth;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ActivatePassengerRequest {

    @SerializedName("token")
    @Expose
    private String token;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

}