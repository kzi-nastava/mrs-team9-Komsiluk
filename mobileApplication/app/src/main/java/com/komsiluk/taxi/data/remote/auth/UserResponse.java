package com.komsiluk.taxi.data.remote.auth;

import com.komsiluk.taxi.auth.UserRole;

public class UserResponse {
    private Long id;
    private String email;
    private UserRole role;
    private String driverStatus;

    public UserResponse() {}

    public Long getId() { return id; }
    public String getEmail() { return email; }
    public UserRole getRole() { return role; }
    public String getDriverStatus() { return driverStatus; }
}