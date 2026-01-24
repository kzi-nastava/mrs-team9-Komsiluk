package com.komsiluk.taxi.data.remote.auth;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PassengerRegistrationResponse {

    @SerializedName("message")
    @Expose
    private String message;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
