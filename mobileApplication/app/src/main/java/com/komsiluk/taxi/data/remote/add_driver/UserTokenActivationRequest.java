package com.komsiluk.taxi.data.remote.add_driver;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UserTokenActivationRequest {

    @SerializedName("token")
    @Expose
    private String token;

    @SerializedName("password")
    @Expose
    private String password;

    public UserTokenActivationRequest(String token, String password) {
        this.token = token;
        this.password = password;
    }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}
