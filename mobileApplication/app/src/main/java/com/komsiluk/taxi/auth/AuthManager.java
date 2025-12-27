package com.komsiluk.taxi.auth;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class AuthManager {

    private UserRole currentRole = UserRole.GUEST;

    @Inject
    public AuthManager() {}

    public boolean login(String email, String password) {
        if (email.equals("passenger@test.com") && password.equals("12345678")) {
            currentRole = UserRole.PASSENGER;
            return true;
        }
        if (email.equals("driver@test.com") && password.equals("12345678")) {
            currentRole = UserRole.DRIVER;
            return true;
        }
        if (email.equals("admin@test.com") && password.equals("12345678")) {
            currentRole = UserRole.ADMIN;
            return true;
        }
        return false;
    }

    public UserRole getRole() {
        return currentRole;
    }

    public boolean isLoggedIn() {
        return currentRole != UserRole.GUEST;
    }

    public void logout() {
        currentRole = UserRole.GUEST;
    }
}
