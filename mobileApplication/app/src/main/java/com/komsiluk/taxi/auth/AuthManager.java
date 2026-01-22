package com.komsiluk.taxi.auth;

import com.komsiluk.taxi.data.session.SessionManager;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class AuthManager {

    private final SessionManager sessionManager;

    @Inject
    public AuthManager(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    public boolean isLoggedIn() {
        return sessionManager.getToken() != null;
    }

    public UserRole getRole() {
        return sessionManager.getRole();
    }

    public void logout() {
        sessionManager.clear();
    }
}

