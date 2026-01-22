package com.komsiluk.taxi.data.remote.auth;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.komsiluk.taxi.auth.UserRole;

public class LoginResponse {

    @SerializedName("token")
    @Expose
    private String token;
    @SerializedName("id")
    @Expose
    private Long id;
    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("role")
    @Expose
    private UserRole role;
    @SerializedName("driverStatus")
    @Expose
    private Object driverStatus;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    public Object getDriverStatus() {
        return driverStatus;
    }

    public void setDriverStatus(Object driverStatus) {
        this.driverStatus = driverStatus;
    }

}